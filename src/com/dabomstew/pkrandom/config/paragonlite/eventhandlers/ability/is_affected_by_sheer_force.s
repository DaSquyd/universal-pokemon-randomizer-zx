    push    {r3-r7, lr}
    mov     r5, r0
    
    mov     r1, #MVD_FlinchChance
    bl      ARM9::GetMoveData
    cmp     r0, #FALSE
    bne     ReturnTrue
    
CheckQuality:
    mov     r0, r5
    bl      ARM9::GetMoveQuality
    mov     r4, r0

CheckStatus:
    cmp     r4, #MQ_DamageAndStatus
    bne     CheckTargetStatChange
    
    mov     r0, r5
    mov     r1, #MVD_StatusType
    bl      ARM9::GetMoveData
    bne     ReturnTrue
    b       ReturnFalse
    
CheckTargetStatChange:
    cmp     r4, #MQ_DamageAndTargetStatChange
    beq     ReturnTrue
    
CheckUserStatChange:
    cmp     r4, #MQ_DamageAndUserStatChange
    bne     CheckSecretPower
    
    mov     r0, r5
    bl      ARM9::GetMoveStatChangeStat
    mov     r6, r0
    ldr     r4, =0 ; TODO: fix simplification so it doesn't work here somehow
    beq     ReturnTrue
    
    add     r7, sp, #0x00 ; r3
    
CheckUserStatChange_LoopStart:
    mov     r0, r5
    mov     r1, r4
    mov     r2, r7
    bl      ARM9::GetMoveStatChangeStage
    ldr     r0, [sp, #0x00]
    cmp     r0, #0
    bge     CheckUserStatChange_LoopCheckContinue
    b       ReturnFalse
    
CheckUserStatChange_LoopCheckContinue:
    add     r4, #1
    cmp     r4, r6
    bcc     CheckUserStatChange_LoopStart
    b       ReturnTrue
    
; TODO: Make this into a table?
CheckSecretPower:
    ldr     r0, =290
    cmp     r5, r0
    beq     ReturnTrue
    
CheckSpiritShackle:
    lsl     r0, #1
    add     r0, #(662 - (290 << 1))
    cmp     r5, r0
    beq     ReturnTrue
    
CheckSparklingAria:
    add     r0, #(664 - 662)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckAnchorShot:
    add     r0, #(677 - 664)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckGenesisSupernova:
    add     r0, #(703 - 677)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckEerieSpell:
    add     r0, #(826 - 703)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckStoneAxe:
    add     r0, #(830 - 826)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckCeaselessEdge:
    add     r0, #(845 - 830)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckOrderUp:
    add     r0, #(856 - 845)
    cmp     r5, r0
    beq     ReturnTrue
    
CheckElectroShot:
    add     r0, #(905 - 856)
    cmp     r5, r0
    beq     ReturnTrue
    
ReturnFalse:
    mov     r0, #FALSE
    pop     {r3-r7, pc}
    
ReturnTrue:
    mov     r0, #TRUE
    pop     {r3-r7, pc}