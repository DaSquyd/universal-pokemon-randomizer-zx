#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_TARGET_COUNT 0x05
#DEFINE VAR_TARGET_MON_ID_0 0x06

#DEFINE EFFECT_CHANGE_STAT_STAGE 0x0E

#DEFINE STATSTAGE_ATTACK 0x01

    push    {r3-r7, lr}
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return
    
    mov     r0, #VAR_TARGET_COUNT
    bl      Battle::EventVar_GetValue
    mov     r4, #0
    mov     r7, r0
    beq     Return
    
LoopStart:
    add     r0, r4, #VAR_TARGET_MON_ID_0
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     LoopCheckContinue
    
    mov     r0, r5
    mov     r1, #EFFECT_CHANGE_STAT_STAGE
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #1
    ldr     r2, [r1, #0x00] ; get header
    lsl     r0, #23 ; 00000000_10000000_00000000_00000000
    orr     r0, r2
    str     r0, [r1, #0x00]
    
    mov     r0, #STATSTAGE_ATTACK ; attack stat
    str     r0, [r1, #0x04] ; rank type
    mov     r0, #3
    strb    r0, [r1, #0x0C] ; rank volume
    mov     r0, #1
    strb    r0, [r1, #0x0F] ; poke count
    strb    r6, [r1, #0x10] ; poke id
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
LoopCheckContinue:
    add     r4, #1
    cmp     r4, r7
    bcc     LoopStart
    
Return:
    pop     {r3-r7, pc}