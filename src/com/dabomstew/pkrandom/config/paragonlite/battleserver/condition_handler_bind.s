#define MOVE_ViseGrip 11
#define MOVE_Bind 20
#define MOVE_Wrap 35
#define MOVE_FireSpin 83
#define MOVE_Clamp 128
#define MOVE_Whirlpool 250
#define MOVE_SandTomb 328
#define MOVE_MagmaStorm 463
#define MOVE_Infestation 611

#define BTLANM_Bind_Trap 630
#define BTLANM_Wrap_Trap 631
#define BTLANM_FireSpin_Trap 632
#define BTLANM_MagmaStorm_Trap 633
#define BTLANM_Clamp_Trap 634
#define BTLANM_Whirlpool_Trap 635
#define BTLANM_SandTomb_Trap 636

#define S_ServerFlow 0x00
#define S_BattleMon 0x04
#define S_ConditionData 0x08

    push    {r4-r7, lr}
    sub     sp, #0x14
    str     r0, [sp, #S_ServerFlow]
    str     r1, [sp, #S_BattleMon]
    mov     r7, r2 ; pokeId
    
    mov     r0, r1
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Return
    
    ldr     r0, [sp, S_BattleMon]
    mov     r1, #MC_Bind
    bl      Battle::Poke_GetConditionData
    str     r0, [sp, #S_ConditionData]
    bl      Battle::ConditionPtr_GetMove
    mov     r5, r0
    
    mov     r6, #0
    mvn     r6, r6 ; -1
    
    cmp     r5, #MOVE_Clamp
    bgt     GT_Clamp
    blt     LT_Clamp
    
    ldr     r6, =BTLANM_Clamp_Trap
    b       PushWork
    
LT_Clamp:
    cmp     r5, #MOVE_Wrap
    bgt     GT_Wrap
    blt     LT_Wrap
    
    ldr     r6, =BTLANM_Wrap_Trap
    b       PushWork
    
LT_Wrap:
    cmp     r5, #MOVE_Bind
    bne     NE_Bind
    
    ldr     r6, =BTLANM_Bind_Trap
    b       PushWork
    
NE_Bind
#if PARAGONLITE || REDUX
    cmp     r5, #MOVE_ViseGrip
    bne     PushWork
    
    ldr     r6, =BTLANM_ViceGrip_Trap
    b       PushWork
#endif
    
GT_Wrap:
    cmp     r5, #MOVE_FireSpin
    bne     PushWork
    
    mov     r6, #(BTLANM_FireSpin_Trap >> 2)
    b       PushWork_Shift
    
GT_Clamp:
    mov     r1, #MOVE_Clamp
    add     r1, #(MOVE_SandTomb - MOVE_Clamp)
    cmp     r5, r1
    bgt     GT_SandTomb
    blt     LT_SandTomb
    
    mov     r6, #(BTLANM_SandTomb_Trap >> 2)
    b       PushWork_Shift
    
LT_SandTomb:
    cmp     r5, #MOVE_Whirlpool
    bne     PushWork
    
    ldr     r6, =BTLANM_Whirlpool_Trap
    b       PushWork
    
GT_SandTomb:
    add     r1, #(MOVE_MagmaStorm - MOVE_SandTomb)
    cmp     r5, r1
    bne     NE_MagmaStorm
    
    add     r6, r5, #(BTLANM_MagmaStorm_Trap - MOVE_MagmaStorm)
    b       PushWork
    
NE_MagmaStorm:
#if PARAGONLITE || REDUX
    add     r1, #(MOVE_Infestation - MOVE_MagmaStorm)
    cmp     r5, r1
    beq     Infestation
#endif
    b       PushWork
    
    
PushWork_Shift:
    lsl     r6, #2
    
PushWork:
    ldr     r0, [sp, #S_ServerFlow]
    mov     r1, #HE_Damage
    mov     r2, r7
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    strb    r7, [r4, #HandlerParam_Damage.pokeId]
    
    ldr     r0, [sp, #S_ConditionData]
    bl      Battle::ConditionPtr_GetIsFlagged ; Checks for Binding Band
    cmp     r0, #FALSE
    beq     NoBindingBand
    
BindingBand:
    mov     r1, #6 ; 1/6 HP
    b       ApplyDamage
    
NoBindingBand:
    mov     r1, #8 ; 1/8 HP
    
ApplyDamage:
    ldr     r0, [sp, #S_BattleMon]
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r4, #HandlerParam_Damage.damage]
    
    mov     r0, #0
    cmp     r6, r0
    blt     SetMessage
    
ApplyAnim:
    ldrb    r1, [r4, #HandlerParam_Damage.flags]
    mov     r0, #2 ; fExEffect
    orr     r0, r1
    strb    r0, [r4, #HandlerParam_Damage.flags]
    
    strh    r6, [r4, #HandlerParam_Damage.effectNumber]
    
    ldr     r0, [sp, #S_ServerFlow]
    mov     r1, r7
    bl      Battle::Handler_PokeIDToPokePos
    strb    r0, [r4, #HandlerParam_Damage.posFrom]
    mov     r0, #6 ; null
    strb    r0, [r4, #HandlerParam_Damage.posTo]
    
SetMessage:
    mov     r0, r4
    mov     r1, #2
    mov     r2, #(372 >> 2) ; "[poke] is hurt by [move]!"
    lsl     r2, #2
    bl      Battle::Handler_StrSetup
    
    mov     r0, r4
    add     r0, #HandlerParam_Damage.exStr
    mov     r1, r7
    bl      Battle::Handler_AddArg
    
    mov     r0, r4
    add     r0, #HandlerParam_Damage.exStr
    mov     r1, r5
    bl      Battle::Handler_AddArg
    
    ldr     r0, [sp, #S_ServerFlow]
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #0x14
    pop     {r4-r7, pc}
    