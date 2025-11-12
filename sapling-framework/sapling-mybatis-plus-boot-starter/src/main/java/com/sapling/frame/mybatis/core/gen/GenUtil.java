package com.sapling.frame.mybatis.core.gen;


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;

import com.baomidou.mybatisplus.generator.config.*;
import com.sapling.frame.mybatis.core.mapper.BaseMapperX;
import org.springframework.util.ObjectUtils;
import java.util.Objects;
import java.util.Scanner;

/**
 * @Description: 自动生成的util
 * @author: neo
 * @date: 2022/5/7 10:34
 */
public class GenUtil {

    public  final static String JAVA_PATH="/src/main/java";
    public  final static String TARGET_PATH="/target/classes/";
    public final static String AUTHOR = "mofang-gen";


    /**
     * 生成数据源对象
     * @param url jdbc URL
     * @param user 用户名
     * @param pwd 密码
     * @return 数据源对象
     */
    public static DataSourceConfig getDataSourceConfigBuilder(String url, String user, String pwd) {
      return new DataSourceConfig.Builder(url, user, pwd).build();
    }


    /**
     * 代码生成
     * @param genClass 当前类的class对象，用于获取当前module的路径
     * @param dbUrl jdbc URL
     * @param dbUser 数据库用户名
     * @param dbPwd 数据库密码
     * @param ignoreTablePrefix 要忽略的表名前缀
     * @param ignoreTableSuffix 要忽略的表名后缀
     */
    public static void generatorX(Class genClass,String dbUrl,String dbUser,String dbPwd,String[] ignoreTablePrefix,String[] ignoreTableSuffix ){
        AutoGenerator generator = new AutoGenerator(getDataSourceConfigBuilder(dbUrl,dbUser,dbPwd));
        String location = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String modulesPath = location.replace(TARGET_PATH, "");
        //全局配置
        GlobalConfig gc = new GlobalConfig.Builder().outputDir(modulesPath + JAVA_PATH).author(AUTHOR).enableSwagger().disableOpenDir().build();
        generator.global(gc);
        //包配置
        PackageConfig pc = new PackageConfig.Builder().parent(getPackName(genClass)).moduleName(scanner("模块名")).build();
        generator.packageInfo(pc);
        generator.template(new TemplateConfig.Builder().controller("/codeGen/controller.java.vm").build());
        //策略配置
        StrategyConfig.Builder strategyBuilder = new StrategyConfig.Builder();
        if (!ObjectUtils.isEmpty(ignoreTablePrefix)){
            strategyBuilder.addTablePrefix(ignoreTablePrefix);
        }
        if (!ObjectUtils.isEmpty(ignoreTableSuffix)){
            strategyBuilder.addTableSuffix(ignoreTableSuffix);
        }
        StrategyConfig strategy = strategyBuilder.addInclude(scanner("表名，多个名表请用英文逗号分隔").split(","))
                .entityBuilder().enableLombok().enableTableFieldAnnotation().build()
                .controllerBuilder().enableRestStyle().build()
                .mapperBuilder().superClass(BaseMapperX.class).enableBaseColumnList().enableBaseResultMap().enableMapperAnnotation()
                .build();
        generator.strategy(strategy);
        generator.execute();
    }

    /**
     * 获取包名
     *
     * @return net.zf.x
     */
    public static String getPackName(Class genClass) {
        Package pack = genClass.getPackage();
        String packName = pack.getName();
        do {
            packName = packName.substring(0, packName.lastIndexOf("."));
            pack = Package.getPackage(packName);
        } while (null != pack);
        return packName;
    }

    /**
     *
     * @param tip
     * @return
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(" " + tip + ":");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (!ObjectUtils.isEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "!");
    }
}
