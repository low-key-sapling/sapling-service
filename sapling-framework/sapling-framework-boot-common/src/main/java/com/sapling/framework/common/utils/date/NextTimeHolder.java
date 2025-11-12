package com.sapling.framework.common.utils.date;

import com.sapling.framework.common.utils.object.LockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

public class NextTimeHolder {

    private static final Logger logger = LoggerFactory.getLogger(NextTimeHolder.class);

    public static AtomicLong nextTime = new AtomicLong(0);


    /**
     * 此方法为线程安全
     * 尝试将nextTime修改为newTime;
     * 因为是循环CAS操作,修改成功为止
     * 所以如果newTime< nextTime则修改为newTime
     * 否则还是修改为nextTime;
     * 如果修改之后的值是newTime ;则需要通知一下LockUtil.lock 醒过来;
     * 如果仍旧是nextTime说明要么当前的执行时间很后面或者说被另外一个线程修改为了更小的值
     * @param newTime  新的执行时间
     */
    public static void tryUpdate(long newTime) {
        LongUnaryOperator updateFunction = (currentVal) -> {
            if (newTime < currentVal) { // 使用传入的currentVal而非实时get()
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedCurrentVal = sdf.format(new Date(currentVal));
                String formattedNewTime = sdf.format(new Date(newTime));
                logger.warn("更新nextTime: {} ({}) -> {} ({})", currentVal, formattedCurrentVal, newTime, formattedNewTime);
                return newTime;
            }
            return currentVal; // 保持原值
        };

        long prev = nextTime.getAndUpdate(updateFunction);
        long updated = nextTime.get();

        // 当且仅当实际发生更新时才唤醒
        if (updated < prev) {
            synchronized (LockUtil.lock) {
                LockUtil.lock.notifyAll();
            }
        }
    }



    /**
     * 将nextTime设置为0之后  并且通知  就会立马执行一次搬运操作
     */
    public static void setZeroAndNotify(){
        synchronized (LockUtil.lock){
            NextTimeHolder.nextTime.set(0);
            LockUtil.lock.notifyAll();
        }

    }
}
