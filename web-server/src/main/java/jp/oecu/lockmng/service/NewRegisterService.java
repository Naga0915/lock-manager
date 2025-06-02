package jp.oecu.lockmng.service;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.oecu.lockmng.config.properties.RegisterConfig;
import jp.oecu.lockmng.entity.NewRegister;
import jp.oecu.lockmng.repository.jpa.NewRegisterRepository;
import jp.oecu.lockmng.util.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewRegisterService {
    private final NewRegisterRepository newRegisterRepository;
    private final RegisterConfig registerConfig;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public NewRegisterService(NewRegisterRepository newRegisterRepository, RegisterConfig registerConfig, ObjectMapper objectMapper){
        this.newRegisterRepository = newRegisterRepository;
        this.registerConfig = registerConfig;
        this.objectMapper = objectMapper;
    }

    public boolean isValid(String uuid){
        return newRegisterRepository.existsByUuid(uuid);
    }

    public Result<String> getNewRegister(){
        NewRegister newRegister = new NewRegister();
        newRegister.setUuid(UUID.randomUUID().toString());
        newRegister.setCreated(ZonedDateTime.now());
        newRegister.setExpire(newRegister.getCreated().plusHours(registerConfig.getExpire_hour()));
        try{
            NewRegister saved = newRegisterRepository.save(newRegister);
            if(saved == null){
                log.error("新規登録用URLの登録に失敗。：" + objectMapper.writeValueAsString(newRegister));
            }
        }catch (Exception e){
            log.error("新規登録用IDの登録に失敗", e);
            return Result.error(e.getLocalizedMessage());
        }
        return Result.success(newRegister.getUuid());
    }
}
