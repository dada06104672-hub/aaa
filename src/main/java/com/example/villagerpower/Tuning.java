package com.example.villagerpower;

/** 数値調整用。ここを変えるだけでバランスを変えられます。 */
public final class Tuning {
    private Tuning() {}

    /** 回し車の回転数(RPM) */
    public static final float WHEEL_RPM = 32f;
    /** 回し車が供給する応力容量(SU/RPM)。32RPM x 8 = 256 SU */
    public static final float WHEEL_CAPACITY = 8f;

    /** モーター1台が消費する応力(SU/RPM)。32RPMで 4 x 32 = 128 SU */
    public static final float MOTOR_IMPACT = 4f;
    /** 消費した応力1SUあたりに発生するFE/tick。1.0なら 128SU -> 128FE/t */
    public static final float FE_PER_SU = 1.0f;
    /** モーター内部バッファ(FE) */
    public static final int MOTOR_BUFFER = 100_000;
    /** モーターが1tickに外へ出せる最大FE */
    public static final int MOTOR_MAX_OUT = 4_000;
}
