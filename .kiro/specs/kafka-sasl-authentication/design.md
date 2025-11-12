# Design Document

## Overview

This design document describes the implementation approach for adding SASL/PLAIN authentication support to the `endpoint-spring-boot-starter-kafka` module. The solution extends the existing multi-datasource Kafka configuration framework to support secure authentication while maintaining backward compatibility.

## Architecture

### Component Diagram

```
ZfKafkaProperties (Enhanced)
    ├── security: ZfKafkaSecurity (NEW)
    ├── consumer: ZfKafkaConsumer
    ├── producer: ZfKafkaProducer
    └── datasource: Map<String, ZfKafkaProperties>
        └── [datasourceName]
            ├── security: ZfKafkaSecurity (NEW)
            ├── consumer: ZfKafkaConsumer
            └── producer: ZfKafkaProducer

KafkaDynamicSourceRegister (Enhanced)
    ├── producerConfig() → adds SASL properties
    ├── consumerConfig() → adds SASL properties
    └── applySecurity() → NEW method
```

### Data Flow

```
YAML Configuration
    ↓
ZfKafkaProperties (parsed by Spring Boot)
    ↓
KafkaDynamicSourceRegister.afterPropertiesSet()
    ↓
For each datasource:
    ├── getDataSource() → merge parent and child configs
    ├── producerConfig() → build config with security
    ├── consumerConfig() → build config with security
    └── Register KafkaTemplate and ContainerFactory
```

## Components and Interfaces

### 1. ZfKafkaSecurity (NEW)

**Purpose**: Configuration class for Kafka security settings

**Location**: `properties.core.com.sapling.framework.kafka.ZfKafkaSecurity`

**Properties**:
```java
public class ZfKafkaSecurity {
    // Security protocol: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
    private String protocol;
    
    // SASL mechanism: PLAIN, SCRAM-SHA-256, SCRAM-SHA-512
    private String mechanism;
    
    // SASL username
    private String username;
    
    // SASL password
    private String password;
    
    // SSL trust store location (for SASL_SSL)
    private String trustStoreLocation;
    
    // SSL trust store password (for SASL_SSL)
    private String trustStorePassword;
    
    // Additional security properties
    private Map<String, String> properties;
}
```

**Methods**:
- `isEnabled()`: Returns true if security is configured
- `isSaslEnabled()`: Returns true if protocol contains SASL
- `isSslEnabled()`: Returns true if protocol contains SSL
- `buildJaasConfig()`: Constructs JAAS configuration string

### 2. ZfKafkaProperties (ENHANCED)

**Changes**:
- Add `@NestedConfigurationProperty private ZfKafkaSecurity security;`
- Security configuration will be inherited by child datasources

### 3. KafkaDynamicSourceRegister (ENHANCED)

**New Methods**:

```java
/**
 * Apply security configuration to Kafka config map
 */
private void applySecurity(Map<String, Object> config, ZfKafkaSecurity security) {
    if (security == null || !security.isEnabled()) {
        return;
    }
    
    // Add security protocol
    config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, security.getProtocol());
    
    // Add SASL configuration
    if (security.isSaslEnabled()) {
        config.put(SaslConfigs.SASL_MECHANISM, security.getMechanism());
        config.put(SaslConfigs.SASL_JAAS_CONFIG, security.buildJaasConfig());
    }
    
    // Add SSL configuration
    if (security.isSslEnabled()) {
        if (StringUtils.isNotBlank(security.getTrustStoreLocation())) {
            config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, security.getTrustStoreLocation());
        }
        if (StringUtils.isNotBlank(security.getTrustStorePassword())) {
            config.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, security.getTrustStorePassword());
        }
    }
    
    // Add additional properties
    if (security.getProperties() != null) {
        config.putAll(security.getProperties());
    }
}
```

**Modified Methods**:

```java
private Map<String, Object> producerConfig(ZfKafkaProducer producer, ZfKafkaSecurity security) {
    Map<String, Object> producerConfigs = new HashMap<>(16);
    // ... existing configuration ...
    
    // Apply security configuration
    applySecurity(producerConfigs, security);
    
    return producerConfigs;
}

private Map<String, Object> consumerConfig(ZfKafkaConsumer consumer, ZfKafkaSecurity security) {
    Map<String, Object> consumerConfigs = new HashMap<>(16);
    // ... existing configuration ...
    
    // Apply security configuration
    applySecurity(consumerConfigs, security);
    
    return consumerConfigs;
}
```

**Enhanced getDataSource Method**:
```java
private ZfKafkaProperties getDataSource(ZfKafkaProperties datasource) {
    ZfKafkaProperties propertiesWrapper = new ZfKafkaProperties();
    BeanUtil.copyProperties(zfKafkaProperties, propertiesWrapper);
    
    // ... existing logic ...
    
    // Merge security configuration
    if (ObjectUtils.isNotEmpty(datasource.getSecurity())) {
        if (propertiesWrapper.getSecurity() == null) {
            propertiesWrapper.setSecurity(new ZfKafkaSecurity());
        }
        BeanUtil.copyProperties(datasource.getSecurity(), propertiesWrapper.getSecurity(), 
            CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    }
    
    return propertiesWrapper;
}
```

## Data Models

### Configuration Structure

```yaml
zf:
  kafka:
    dynamic:
      primary: mbws
      datasource:
        mbws:
          bootstrap-servers:
            - 192.168.126.130:9092
          security:
            protocol: SASL_PLAINTEXT
            mechanism: PLAIN
            username: kafka-user
            password: kafka-password
          consumer:
            container-factory: mbwsKafkaListenerContainerFactory
            group-id: sapling-group
            auto-offset-reset: latest
          producer:
            kafka-template: mbwsKafkaTemplate
            
        secure-cluster:
          bootstrap-servers:
            - secure-kafka.example.com:9093
          security:
            protocol: SASL_SSL
            mechanism: PLAIN
            username: secure-user
            password: secure-password
            trust-store-location: file:./jks/client_truststore.jks
            trust-store-password: truststore-password
            properties:
              ssl.endpoint.identification.algorithm: ""
              request.timeout.ms: 60000
          consumer:
            container-factory: secureKafkaListenerContainerFactory
            group-id: secure-group
          producer:
            kafka-template: secureKafkaTemplate
```

### JAAS Configuration Format

For SASL/PLAIN, the JAAS configuration string format:
```
org.apache.kafka.common.security.plain.PlainLoginModule required username="<username>" password="<password>";
```

## Error Handling

### Validation Rules

1. **Security Protocol Validation**
   - If security is configured, protocol must be one of: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
   - Default to PLAINTEXT if not specified

2. **SASL Configuration Validation**
   - If protocol contains SASL, mechanism must be specified
   - If mechanism is PLAIN, username and password are required
   - Throw IllegalArgumentException with clear message if validation fails

3. **SSL Configuration Validation**
   - If protocol is SASL_SSL, trust-store-location should be provided
   - Log warning if SSL is enabled but trust store is not configured

### Error Messages

```java
// In ZfKafkaSecurity
public void validate() {
    if (!isEnabled()) {
        return;
    }
    
    if (isSaslEnabled()) {
        Assert.hasText(mechanism, "SASL mechanism must be specified when using SASL protocol");
        
        if ("PLAIN".equalsIgnoreCase(mechanism)) {
            Assert.hasText(username, "Username is required for SASL/PLAIN authentication");
            Assert.hasText(password, "Password is required for SASL/PLAIN authentication");
        }
    }
    
    if (isSslEnabled() && StringUtils.isBlank(trustStoreLocation)) {
        log.warn("SSL is enabled but trust store location is not configured. " +
                "This may cause connection failures.");
    }
}
```

### Logging Strategy

1. **Startup Logging**
   - Log security protocol for each datasource (without credentials)
   - Example: "Kafka datasource [mbws] configured with security protocol: SASL_PLAINTEXT"

2. **Sensitive Data Protection**
   - Never log passwords or JAAS configuration strings
   - Mask username in logs: "user***"

3. **Error Logging**
   - Log authentication failures with datasource name
   - Provide actionable error messages

## Testing Strategy

### Unit Tests

1. **ZfKafkaSecurity Tests**
   - Test `isEnabled()` with various configurations
   - Test `buildJaasConfig()` output format
   - Test validation logic for different scenarios

2. **KafkaDynamicSourceRegister Tests**
   - Test `applySecurity()` with SASL_PLAINTEXT
   - Test `applySecurity()` with SASL_SSL
   - Test `applySecurity()` with null security (backward compatibility)
   - Test configuration merging with parent and child security settings

### Integration Tests

1. **Single Datasource with SASL/PLAIN**
   - Configure one datasource with SASL_PLAINTEXT
   - Verify KafkaTemplate can send messages
   - Verify listener can receive messages

2. **Multiple Datasources with Different Security**
   - Configure one datasource with SASL, one without
   - Verify both work independently
   - Verify correct security applied to each

3. **SASL_SSL Configuration**
   - Configure datasource with SASL_SSL and trust store
   - Verify SSL handshake succeeds
   - Verify authentication succeeds

### Manual Testing

1. Test with actual Kafka cluster requiring SASL/PLAIN
2. Test with invalid credentials (should fail gracefully)
3. Test backward compatibility with existing non-authenticated setup

## Security Considerations

### Password Management

1. **Configuration File Security**
   - Recommend using encrypted passwords in configuration
   - Support Spring Boot's encrypted property values (e.g., `enc`xxx`)
   - Document best practices for credential management

2. **Environment Variables**
   - Support reading credentials from environment variables
   - Example: `${KAFKA_USERNAME}` and `${KAFKA_PASSWORD}`

3. **External Configuration**
   - Support Spring Cloud Config for centralized credential management
   - Support Kubernetes secrets mounting

### SSL/TLS Best Practices

1. **Trust Store Management**
   - Support both classpath and file system paths
   - Validate trust store file exists at startup
   - Support encrypted trust store passwords

2. **Certificate Validation**
   - Enable hostname verification by default
   - Allow disabling for development (with warning)

## Performance Considerations

1. **Configuration Parsing**
   - Security configuration parsed once at startup
   - No runtime overhead for authentication

2. **Connection Pooling**
   - SASL authentication happens during connection establishment
   - Existing connection pooling mechanisms remain unchanged

3. **Memory Usage**
   - Minimal additional memory for security configuration objects
   - JAAS configuration strings cached per datasource

## Backward Compatibility

### Existing Configurations

All existing configurations without security settings will continue to work:

```yaml
# This continues to work without changes
zf:
  kafka:
    dynamic:
      primary: local
      datasource:
        local:
          bootstrap-servers:
            - localhost:9092
          consumer:
            container-factory: localKafkaListenerContainerFactory
          producer:
            kafka-template: localKafkaTemplate
```

### Migration Path

For users wanting to add authentication:

1. Add security section to datasource configuration
2. No code changes required
3. Restart application

## Dependencies

### Required Kafka Client Libraries

Already included in the module:
- `org.apache.kafka:kafka-clients` - Contains SASL/PLAIN implementation
- `org.springframework.kafka:spring-kafka` - Spring Kafka support

No additional dependencies required.

## Configuration Examples

### Example 1: SASL_PLAINTEXT (No SSL)

```yaml
zf:
  kafka:
    dynamic:
      primary: dev
      datasource:
        dev:
          bootstrap-servers:
            - kafka-dev.example.com:9092
          security:
            protocol: SASL_PLAINTEXT
            mechanism: PLAIN
            username: dev-user
            password: dev-password
          consumer:
            container-factory: devKafkaListenerContainerFactory
            group-id: dev-group
          producer:
            kafka-template: devKafkaTemplate
```

### Example 2: SASL_SSL (With SSL)

```yaml
zf:
  kafka:
    dynamic:
      primary: prod
      datasource:
        prod:
          bootstrap-servers:
            - kafka-prod.example.com:9093
          security:
            protocol: SASL_SSL
            mechanism: PLAIN
            username: ${KAFKA_USERNAME}
            password: ${KAFKA_PASSWORD}
            trust-store-location: file:./jks/client_truststore.jks
            trust-store-password: ${TRUSTSTORE_PASSWORD}
            properties:
              ssl.endpoint.identification.algorithm: ""
          consumer:
            container-factory: prodKafkaListenerContainerFactory
            group-id: prod-group
          producer:
            kafka-template: prodKafkaTemplate
```

### Example 3: Mixed Security (Multiple Datasources)

```yaml
zf:
  kafka:
    dynamic:
      primary: local
      datasource:
        local:
          bootstrap-servers:
            - localhost:9092
          # No security - backward compatible
          consumer:
            container-factory: localKafkaListenerContainerFactory
          producer:
            kafka-template: localKafkaTemplate
            
        secure:
          bootstrap-servers:
            - secure-kafka.example.com:9093
          security:
            protocol: SASL_SSL
            mechanism: PLAIN
            username: secure-user
            password: secure-password
            trust-store-location: classpath:jks/truststore.jks
            trust-store-password: truststore-pass
          consumer:
            container-factory: secureKafkaListenerContainerFactory
          producer:
            kafka-template: secureKafkaTemplate
```

## Implementation Notes

### Import Statements Required

```java
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
```

### JAAS Configuration Builder

```java
public String buildJaasConfig() {
    if (!"PLAIN".equalsIgnoreCase(mechanism)) {
        throw new UnsupportedOperationException(
            "Only PLAIN mechanism is currently supported");
    }
    
    return String.format(
        "org.apache.kafka.common.security.plain.PlainLoginModule required " +
        "username=\"%s\" password=\"%s\";",
        username, password
    );
}
```

## Future Enhancements

1. **Additional SASL Mechanisms**
   - SCRAM-SHA-256
   - SCRAM-SHA-512
   - GSSAPI (Kerberos)

2. **Mutual TLS (mTLS)**
   - Client certificate authentication
   - Key store configuration

3. **OAuth/OIDC Support**
   - Token-based authentication
   - Integration with OAuth providers

4. **Credential Rotation**
   - Dynamic credential refresh
   - Integration with secret management systems
