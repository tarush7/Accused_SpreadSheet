package com.cctns.apprehend.configuration;

import com.cache.service.CacheService;
import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.extport.MsComms;
import com.cctns.apprehend.core.repository.AccusedInfoRepository;
import com.cctns.apprehend.core.repository.ApprehendPrepareRepository;
import com.cctns.apprehend.core.repository.ApprehendSubmitRepository;
import com.cctns.apprehend.core.repository.ApprehendViewRepository;
import com.cctns.apprehend.core.repository.JuvenileDisposalRepository;
import com.cctns.apprehend.core.repository.LookUpMasterRepository;
import com.cctns.apprehend.core.repository.SequenceGeneratorRepoService;
import com.cctns.apprehend.core.repository.SocialBackgroundPrepareRepository;
import com.cctns.apprehend.core.repository.SocialBackgroundSubmitRepository;
import com.cctns.apprehend.core.repository.SocialBackgroundViewRepository;
import com.cctns.apprehend.core.repository.SrNoRepository;
import com.cctns.apprehend.core.usecase.ApprehendPrepareUseCaseImpl;
import com.cctns.apprehend.core.usecase.ApprehendSubmitUseCaseImpl;
import com.cctns.apprehend.core.usecase.ApprehendViewUseCaseImpl;
import com.cctns.apprehend.core.usecase.JuvenileDisposalUseCaseImpl;
import com.cctns.apprehend.core.usecase.SocialBackgroundPrepareUseCaseImpl;
import com.cctns.apprehend.core.usecase.SocialBackgroundSubmitUseCaseImpl;
import com.cctns.apprehend.core.usecase.SocialBackgroundViewUseCaseImpl;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.mapper.FileUploadMapper;
import com.cctns.apprehend.persistence.implementation.LookUpValueRepository;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Cache;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DefaultConfiguration {

    @Value("${spring.datasource.url}")
    String jdbcUrl;

    @Bean
    public ApprehendSubmitUseCaseImpl apprehendSubmitUseCaseImpl(ApprehendSubmitRepository apprehendSubmitRepository, AccusedInfoRepository accusedInfoRepository,
                                                                 SrNoRepository srNoRepository, SequenceGeneratorRepoService sequenceGeneratorRepoService,
                                                                 EntityDomainMapper entityDomainMapper, FileUploadMapper fileUploadMapper,
                                                                 MsComms msComms) {
        return new ApprehendSubmitUseCaseImpl(apprehendSubmitRepository,accusedInfoRepository, srNoRepository,sequenceGeneratorRepoService,
                entityDomainMapper,fileUploadMapper,msComms);
    }

    @Bean
    public ApprehendViewUseCaseImpl apprehendViewUseCaseImpl(ApprehendViewRepository apprehendViewRepository,SocialBackgroundViewRepository socialBackgroundViewRepository, LookUpValueRepository lookUpValueRepository, CacheService cacheService){
        return new ApprehendViewUseCaseImpl(apprehendViewRepository,socialBackgroundViewRepository,lookUpValueRepository,cacheService);
    }

    @Bean
    public ApprehendPrepareUseCaseImpl apprehendPrepareUseCaseImpl(ApprehendPrepareRepository apprehendPrepareRepository){
        return new ApprehendPrepareUseCaseImpl(apprehendPrepareRepository);
    }

    @Bean
    public SocialBackgroundSubmitUseCaseImpl socialBackgroundSubmitUseCaseImpl(SocialBackgroundSubmitRepository socialBackgroundSubmitRepository,FileUploadMapper fileUploadMapper, MsComms msComms){
        return new SocialBackgroundSubmitUseCaseImpl(socialBackgroundSubmitRepository,fileUploadMapper,msComms);
    }

    @Bean
    public SocialBackgroundViewUseCaseImpl socialBackgroundViewUseCaseImpl(SocialBackgroundViewRepository socialBackgroundViewRepository,ApprehendViewRepository apprehendViewRepository,
                                                                           LookUpMasterRepository lookUpMasterRepository,CacheService cacheService){
        return new SocialBackgroundViewUseCaseImpl(socialBackgroundViewRepository,apprehendViewRepository,lookUpMasterRepository,cacheService);
    }

    @Bean
    public SocialBackgroundPrepareUseCaseImpl socialBackgroundPrepareUseCaseImpl(SocialBackgroundPrepareRepository socialBackgroundPrepareRepository,
                                                                                 SocialBackgroundViewRepository socialBackgroundViewRepository){
        return new SocialBackgroundPrepareUseCaseImpl(socialBackgroundPrepareRepository,socialBackgroundViewRepository);
    }

    @Bean
    public JuvenileDisposalUseCaseImpl juvenileDisposalUseCaseImpl(JuvenileDisposalRepository juvenileDisposalRepository, FileUploadMapper fileUploadMapper,
                                                                   MsComms msComms, CacheService cacheService){
        return new JuvenileDisposalUseCaseImpl(juvenileDisposalRepository,fileUploadMapper,msComms,cacheService);
    }

    /**
     * This is the bean declaration of a hashmap which contains
     * key->Class<?> (All classes in com.cctns.complaint.persistence.entity package)
     * value->String(table name)
     */
    @Bean
    public Map<Class<?>, Map<String, String>> tableIdentityRegistry(EntityManager entityManager) {
        Map<Class<?>, Map<String, String>> registry = new HashMap<>();
        Metamodel metamodel = entityManager.getMetamodel();

        for (EntityType<?> entity : metamodel.getEntities()) {
            Class<?> javaType = entity.getJavaType();
            Map<String, String> info = new HashMap<>();

            // 1. Get Table Name using your helper
            info.put(Constants.TABLE_NAME, getTableName(javaType));

            // 2. Get Identity (Primary Key) column name
            info.put(Constants.ID_COLUMN, getIdColumnName(javaType));

            registry.put(javaType, info);
        }
        return registry;
    }

    private String getIdColumnName(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    // If @Column is present on the ID field, get its name, else use field name
                    if (field.isAnnotationPresent(Column.class)) {
                        String name = field.getAnnotation(Column.class).name();
                        return name.isEmpty() ? field.getName() : name;
                    }
                    return field.getName();
                }
            }
            current = current.getSuperclass();
        }
        return Constants.UNKNOWN_ID;
    }

    private String getTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            if (!table.schema().isEmpty()) {
                return table.schema() + "." + table.name();
            } else {
                return table.name();
            }
        }
        // Fallback if @Table is missing but @Entity is present
        return clazz.getSimpleName();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.modulesToInstall(module);
            //   builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

}
