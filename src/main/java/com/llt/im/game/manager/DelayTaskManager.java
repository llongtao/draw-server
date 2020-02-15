package com.llt.im.game.manager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.llt.im.game.model.Group;
import com.llt.im.game.model.Message;
import com.llt.im.utils.MessageUtills;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DelayTaskManager {

    private static Random random = new Random();

    @JsonIgnore
    private static DelayQueue<Notify> notifyQueue = new DelayQueue<>();

    static {
        @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
        Thread thread = new Thread(() -> {
            //noinspection AlibabaAvoidManuallyCreateThread
            while (true) {
                try {
                    Notify notify = notifyQueue.take();
                    Group group = notify.group;
                    if (notify.count == group.getCount()) {
                        if (notify.type == Type.stop) {
                            group.next(MessageUtills.message(new Message("法官", "无人猜对,正确答案:" + group.getKeyWord().getName() + " 进入下一轮")));
                        } else if (notify.type == Type.remind) {
                            if (group.getKeyWord() != null) {
                                group.sendAll(MessageUtills.message(new Message("法官", "提示！！ " + group.getKeyWord().getDesc())));
                            }
                        } else if (notify.type == Type.robotAnswer) {
                            notifyQueue.add(new Notify(Type.robotAnswer, notify.count, group));
                            group.robotAnswer();
                        } else if (notify.type == Type.robotSelect) {
                            group.robotDraw();
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

        });
        thread.start();
    }

    public static void execute(Type type, int count, Group group) {
        notifyQueue.add(new Notify(type, count, group));
    }
    public static void execute(Type type, int count, Group group,long delayTime) {
        notifyQueue.add(new Notify(type, count, group,delayTime));
    }

    public static class Notify implements Delayed {

        private Type type;

        private int count;


        private long delayTime;

        private long putTime;

        private Group group;

        public Notify(Type type, int count, Group group) {
            this.count = count;
            this.type = type;
            this.group = group;
            if (type == Type.remind) {
                this.delayTime = 20000;
            } else if (type == Type.stop) {
                this.delayTime = 60000;
            } else if (type == Type.robotAnswer) {
                this.delayTime = random.nextInt(4000) + 3000;
            } else if (type == Type.robotSelect) {
                this.delayTime = 3000;
            }
            this.putTime = System.currentTimeMillis();
        }
        public Notify(Type type, int count, Group group,long delayTime) {
            this(type,count,group);
            this.delayTime = delayTime;
        }

        /**
         * 用于返回剩余时间
         * 消息是否到期则是通过此方法判断
         * 返回小于等于0则到期
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(putTime + delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * 因为Delayed继承于Comparable
         * 所以需要实现compareTo方法,用于排序
         * 该对象(this)小于、等于或大于指定对象(o)，则分别返回负整数、零或正整数。
         */
        @Override
        public int compareTo(Delayed o) {
            long result = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;
            }
        }


    }

    public enum Type {
        /**
         *
         */
        remind,
        stop,
        robotAnswer,
        robotSelect
    }
}
