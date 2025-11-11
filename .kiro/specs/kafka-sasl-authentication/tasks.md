# Implementation Plan

- [x] 1. Create ZfKafkaSecurity configuration class


  - Create new class `ZfKafkaSecurity.java` in `net.zf.framework.kafka.core.properties` package
  - Add properties: protocol, mechanism, username, password, trustStoreLocation, trustStorePassword, properties map
  - Implement `isEnabled()`, `isSaslEnabled()`, `isSslEnabled()` helper methods
  - Implement `buildJaasConfig()` method to construct JAAS configuration string for SASL/PLAIN
  - Implement `validate()` method with validation logic for required fields
  - Add Lombok annotations (@Data) and JavaDoc comments
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 3.1, 3.4_

- [x] 2. Enhance ZfKafkaProperties to include security configuration


  - Add `@NestedConfigurationProperty private ZfKafkaSecurity security;` field to ZfKafkaProperties class
  - Add getter and setter methods for security field
  - Update JavaDoc to document security configuration support
  - _Requirements: 1.5, 3.2, 3.3_

- [x] 3. Enhance KafkaDynamicSourceRegister with security support


- [x] 3.1 Create applySecurity method


  - Add private method `applySecurity(Map<String, Object> config, ZfKafkaSecurity security)`
  - Implement logic to add CommonClientConfigs.SECURITY_PROTOCOL_CONFIG
  - Implement logic to add SaslConfigs.SASL_MECHANISM and SaslConfigs.SASL_JAAS_CONFIG for SASL
  - Implement logic to add SslConfigs for SSL trust store configuration
  - Add logic to merge additional properties from security.getProperties()
  - Add null-safety checks and early return if security is not enabled
  - Add required imports: CommonClientConfigs, SaslConfigs, SslConfigs
  - _Requirements: 4.1, 4.2, 4.3, 4.4_


- [ ] 3.2 Modify producerConfig method
  - Update method signature to accept ZfKafkaSecurity parameter: `producerConfig(ZfKafkaProducer producer, ZfKafkaSecurity security)`
  - Call `applySecurity(producerConfigs, security)` before returning configuration map


  - _Requirements: 4.1, 4.3_

- [x] 3.3 Modify consumerConfig method


  - Update method signature to accept ZfKafkaSecurity parameter: `consumerConfig(ZfKafkaConsumer consumer, ZfKafkaSecurity security)`
  - Call `applySecurity(consumerConfigs, security)` before returning configuration map


  - _Requirements: 4.2, 4.3_



- [ ] 3.4 Update registerKafkaTemplate method
  - Pass datasource.getSecurity() to producerConfig() method call
  - _Requirements: 4.1_


- [ ] 3.5 Update registerContainerFactory method
  - Pass datasource.getSecurity() to consumerConfig() method call
  - _Requirements: 4.2_



- [ ] 3.6 Enhance getDataSource method for security merging
  - Add logic to merge security configuration from parent to child datasource
  - Use BeanUtil.copyProperties with CopyOptions to merge security settings
  - Ensure child security settings override parent settings when specified


  - _Requirements: 2.3_

- [ ] 3.7 Add security logging
  - Add log statement in registerKafkaTemplate to log security protocol (without credentials)
  - Add log statement in registerContainerFactory to log security protocol (without credentials)
  - Ensure passwords and JAAS configs are never logged
  - _Requirements: 4.5_

- [ ] 4. Update configuration examples in application-kafka.yaml
  - Add commented example for SASL_PLAINTEXT configuration
  - Add commented example for SASL_SSL configuration with trust store
  - Add comments explaining each security property
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 5. Create documentation file for SASL authentication
  - Create `docs/kafka-sasl-authentication.md` file
  - Document all security configuration properties with descriptions
  - Provide complete YAML examples for SASL_PLAINTEXT
  - Provide complete YAML examples for SASL_SSL
  - Provide example with multiple datasources having different security settings
  - Document best practices for credential management (environment variables, encrypted properties)
  - Document troubleshooting tips for common authentication issues
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ]* 6. Write unit tests for ZfKafkaSecurity
  - Test `isEnabled()` returns false when protocol is null or empty
  - Test `isEnabled()` returns true when protocol is set
  - Test `isSaslEnabled()` returns true for SASL_PLAINTEXT and SASL_SSL
  - Test `isSslEnabled()` returns true for SSL and SASL_SSL
  - Test `buildJaasConfig()` generates correct JAAS string format
  - Test `validate()` throws exception when SASL is enabled but username/password missing
  - Test `validate()` succeeds when all required fields are present
  - _Requirements: 1.1, 1.2, 1.3, 3.4_

- [ ]* 7. Write unit tests for KafkaDynamicSourceRegister security methods
  - Test `applySecurity()` adds correct properties for SASL_PLAINTEXT
  - Test `applySecurity()` adds correct properties for SASL_SSL
  - Test `applySecurity()` handles null security gracefully (backward compatibility)
  - Test `applySecurity()` merges additional properties from security.getProperties()
  - Test producerConfig includes security properties when security is configured
  - Test consumerConfig includes security properties when security is configured
  - Test getDataSource merges parent and child security configurations correctly
  - _Requirements: 2.3, 4.1, 4.2, 4.3, 5.1, 5.2_
