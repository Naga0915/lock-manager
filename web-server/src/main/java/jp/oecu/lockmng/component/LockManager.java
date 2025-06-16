package jp.oecu.lockmng.component;

import java.lang.StackWalker.Option;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import jp.oecu.lockmng.LockmngApplication;
import jp.oecu.lockmng.device.LockDevice;
import jp.oecu.lockmng.device.LockInfo;
import jp.oecu.lockmng.device.LockState;
import jp.oecu.lockmng.util.Result;

@Component
public class LockManager {

    private final LockmngApplication lockmngApplication;
    /*
     * espとのメッセージ定義
     * サーバからESP：
     * "YOUR_ID_IS X" 端末にIDを知らせる このIDはサーバ側で割り当てる
     * 
     * "UNLOCK Y" 鍵ID Y を開ける
     * "LOCK Y" 鍵ID Y を閉める
     * "IS_ALIVE X" 端末ID X の生存確認
     * ESPからサーバ
     * "MY_ID_IS X" 端末のIDを設定したことを伝える
     * "I_HAVE Y" 端末の鍵の数を知らせる
     * 
     * "UNLOCK Y SUCCESS" 鍵ID Y を開けました
     * "LOCK Y SUCCESS" 鍵ID Y を閉めました
     * "UNLOCK Y FAILED" 鍵ID Y を開けれませんでした
     * "LOCK Y FAILED" 鍵ID Y を閉めれませんでした
     * "IM_ALIVE X" 端末ID X の生存確認応答
     * 
     * ESP内部での鍵IDは0, 1, 2
     * サーバ側で鍵の数を把握して置く必要がある
     * 
     * ESPは接続初期化時にすべての鍵を閉める必要がある。
     */
    private List<LockDevice> deviceList = new CopyOnWriteArrayList<LockDevice>();
    private Map<Integer, Integer> lockMap = new ConcurrentHashMap<Integer, Integer>();
    private AtomicInteger availableDeviceNum = new AtomicInteger(0);
    private AtomicInteger availableLockNum = new AtomicInteger(0);

    LockManager(LockmngApplication lockmngApplication) {
        this.lockmngApplication = lockmngApplication;
    }

    public Optional<String> addDevice(int lockNum) {
        if (lockNum < 1) {
            return Optional.of("最低でも1つの鍵が必要");
        }
        int deviceId = availableDeviceNum.getAndIncrement();
        int lockId = availableLockNum.getAndAdd(lockNum);
        LockDevice device = new LockDevice(deviceId, lockNum, lockId, true);
        for(LockInfo info : device.getLockList()){
            lockMap.put(info.getServerSideId(), deviceId);
        }
        deviceList.add(device);
        return Optional.empty();
    }

    public Optional<String> operation(int lockId, LockState state) {
        if (lockId < 0) {
            return Optional.of("鍵IDは0以上");
        }
        if(!lockMap.containsKey(lockId)){
            return Optional.of("そのIDの鍵は存在しません");
        }
        List<LockInfo> lockList = deviceList.get(lockMap.get(lockId)).getLockList();
        Boolean found = false;
        int index = 0;
        for (LockInfo lockInfo : lockList) {
            if(lockInfo.getServerSideId() == lockId){
                found = true;
                break;
            }
            index++;
        }
        if(!found){
            return Optional.of("そのIDの鍵は存在しません");
        }

        //TODO: 施錠解錠処理
        lockList.get(index).setState(state);
        
        return Optional.empty();
    }
}
