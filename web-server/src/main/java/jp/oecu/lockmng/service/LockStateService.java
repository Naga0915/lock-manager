package jp.oecu.lockmng.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.entity.LockState;
import jp.oecu.lockmng.repository.jpa.LockStateRepository;
import jp.oecu.lockmng.util.Result;

@Service
public class LockStateService {

    private final LockStateRepository lockStateRepository;

    @Autowired
    public LockStateService(LockStateRepository lockStateRepository) {
        this.lockStateRepository = lockStateRepository;
    }

    public Result<LockState> getById(int id) {
        try {
            Optional<LockState> optional = lockStateRepository.findById(id);
            return optional.map(Result::success)
                           .orElseGet(() -> Result.error("指定されたIDの状態が見つかりません"));
        } catch (Exception e) {
            return Result.error("取得エラー: " + e.getMessage());
        }
    }

    public Result<LockState> createOrUpdate(Integer lockId, String state) {
        try {
            LockState lockState = new LockState();
            lockState.setId(lockId);
            lockState.setState(state);
            LockState saved = lockStateRepository.save(lockState);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("保存エラー: " + e.getMessage());
        }
    }
}