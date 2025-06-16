package jp.oecu.lockmng.service;

import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.entity.LockHist;
import jp.oecu.lockmng.repository.jpa.LockHistRepository;
import jp.oecu.lockmng.util.Result;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LockHistService {
    private final TimeZoneConfig timeZoneConfig;
    private final LockHistRepository lockHistRepository;

    public LockHistService(LockHistRepository lockHistRepository, TimeZoneConfig timeZoneConfig) {
        this.timeZoneConfig = timeZoneConfig;
        this.lockHistRepository = lockHistRepository;
    }

    public Result<List<LockHist>> findAll() {
        try {
            List<LockHist> list = lockHistRepository.findAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("取得エラー: " + e.getMessage());
        }
    }

    public Result<List<LockHist>> findByUserId(Integer userId) {
        try {
            List<LockHist> list = lockHistRepository.findByUserId(userId);

            // UTC → Asia/Tokyo に変換
            list.forEach(hist -> hist.setDatetimeUtc(
                hist.getDatetimeUtc().withZoneSameInstant(timeZoneConfig.getZoneId())
            ));

            return Result.success(list);
        } catch (Exception e) {
            return Result.error("取得エラー: " + e.getMessage());
        }
    }

    public Result<List<LockHist>> findByLockId(Integer lockId) {
        try {
            List<LockHist> list = lockHistRepository.findByLockId(lockId);

            // UTC → Asia/Tokyo に変換
            list.forEach(hist -> hist.setDatetimeUtc(
                hist.getDatetimeUtc().withZoneSameInstant(timeZoneConfig.getZoneId())
            ));

            return Result.success(list);
        } catch (Exception e) {
            return Result.error("取得エラー: " + e.getMessage());
        }
    }

    public Result<LockHist> createAndSaveLockHist(Integer userId, Integer lockId, String ops) {
        try {
            LockHist lockHist = new LockHist();
            lockHist.setUserId(userId);
            lockHist.setLockId(lockId);
            lockHist.setOps(ops);

            lockHist.setDatetimeUtc(ZonedDateTime.now());

            LockHist saved = lockHistRepository.save(lockHist);
            return Result.success(saved);

        } catch (Exception e) {
            return Result.error("保存エラー: " + e.getMessage());
        }
    }

    // public Result<Void> deleteById(Integer id) {
    //     try {
    //         lockHistRepository.deleteById(id);
    //         return Result.success(null);
    //     } catch (Exception e) {
    //         return Result.error("削除エラー: " + e.getMessage());
    //     }
    // }
}
