package jp.oecu.lockmng.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.config.properties.RegisterConfig;
import jp.oecu.lockmng.config.properties.ReservationConfig;
import jp.oecu.lockmng.entity.NewRegister;
import jp.oecu.lockmng.model.NewRegisterModel;
import jp.oecu.lockmng.repository.jpa.NewRegisterRepository;
import jp.oecu.lockmng.util.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewRegisterService {

    private final ReservationConfig reservationConfig;
    private final NewRegisterRepository newRegisterRepository;
    private final RegisterConfig registerConfig;
    private final ObjectMapper objectMapper;
    private final TimeZoneConfig timeZoneConfig;

    @Autowired
    public NewRegisterService(NewRegisterRepository newRegisterRepository, RegisterConfig registerConfig, ObjectMapper objectMapper, TimeZoneConfig timeZoneConfig, ReservationConfig reservationConfig){
        this.newRegisterRepository = newRegisterRepository;
        this.registerConfig = registerConfig;
        this.objectMapper = objectMapper;
        this.timeZoneConfig = timeZoneConfig;
        this.reservationConfig = reservationConfig;
    }

    public Optional<NewRegister> findByUuid(String uuid){
        return newRegisterRepository.findByUuid(uuid);
    }

    public Optional<String> setStatusById(String uuid, boolean status, String userAgent, String ipAddress, String userId){
        try{
            Optional<NewRegister> result = newRegisterRepository.findByUuid(uuid);
            if(!result.isPresent()){
                return Optional.of("無効なUUID");
            }
            NewRegister entity = result.get();
            entity.setEnable(status);
            entity.setIpAddress(ipAddress);
            entity.setUserAgent(userAgent);
            entity.setIdStr(userId);
            newRegisterRepository.save(entity);
        }catch(Exception e){
            return Optional.of("変更を保存できません");
        }
        return Optional.empty();
    }

    public Optional<String> setStatusById(String uuid, boolean status){
        try{
            Optional<NewRegister> result = newRegisterRepository.findByUuid(uuid);
            if(!result.isPresent()){
                return Optional.of("無効なUUID");
            }
            NewRegister entity = result.get();
            entity.setEnable(status);
            newRegisterRepository.save(entity);
        }catch(Exception e){
            return Optional.of("変更を保存できません");
        }
        return Optional.empty();
    }

    public List<NewRegister> findAll(){
        List<NewRegister> list = newRegisterRepository.findAll();
        list.forEach(li -> li.setCreated(li.getCreated().withZoneSameInstant(timeZoneConfig.getZoneId())));
        list.forEach(li -> li.setExpire(li.getExpire().withZoneSameInstant(timeZoneConfig.getZoneId())));
        return list;
    }

    public boolean isValid(String uuid){
        return newRegisterRepository.findByUuid(uuid)
            .map(NewRegister::isEnable)
            .orElse(false);
    }

    public boolean existsByUuid(String uuid){
        return newRegisterRepository.existsByUuid(uuid);
    }

    public Result<String> getNewRegister(NewRegisterModel model){
        NewRegister newRegister = new NewRegister();
        newRegister.setUuid(UUID.randomUUID().toString());
        newRegister.setMemo(model.getMemo());//TODO: メモの最長設定
        newRegister.setCreated(ZonedDateTime.now(timeZoneConfig.getZoneId()));
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
