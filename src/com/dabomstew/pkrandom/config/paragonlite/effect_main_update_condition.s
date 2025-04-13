#DEFINE SP_VAR_40 0x00
#DEFINE SP_IS_FADING_OUT 0x04
#DEFINE SP_DATA_IDX 0x08
#DEFINE SP_ONE_THIRD 0x0C
#DEFINE SP_FIELD_510 0x10
#DEFINE SP_BIT_7 0x14
#DEFINE SP_VAR_28 0x18
#DEFINE SP_TWO 0x1C
#DEFINE SP_DISPLAY_CONDITION 0x20
#DEFINE SP_VAR_1C 0x24

#DEFINE ADD_STACK_SIZE 0x28

#DEFINE CONDITION_COLOR_PARALYSIS 0x01EF ; #7B7B00
#DEFINE CONDITION_COLOR_FROSTBITE 0x7DEF ; #7B7BFF
#DEFINE CONDITION_COLOR_POISON 0x3C0F ; #7B007B
#DEFINE CONDITION_COLOR_BURN 0x000F ; #7B0000

    push    {r3-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    
    mov     r5, #0
    mov     r4, r0 ; r4 := *btlvMcss
    cmp     r5, #8
    bge     ConditionLoop_End
    
    mov     r6, r4
    add     r6, #BtlvMcssData.arr4C
    mov     r7, #0x5C
    
ConditionLoop_Start:
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Mcss_HasDataFile
    cmp     r0, #0
    beq     ConditionLoop_CheckContinue
    
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Mcss_GetDataIndex
    
    mov     r1, r0
    mul     r1, r7
    ldr     r2, [r6, r1]
    lsl     r0, r2, #20
    lsr     r0, #31 ; get: 00000000_00000000_00001000_00000000
    beq     ConditionLoop_CheckContinue
    ldr     r0, =0xFFFFFFF7FF ; mask: 11111111_11111111_11110111_11111111
    and     r0, r2
    str     r0, [r6, r1]
    add     r0, r4, r1
    ldr     r0, [r0, #BtlvMcss.mcssData]
    bl      ARM9::Unk_201B27C
    
ConditionLoop_CheckContinue:
    add     r5, #1
    cmp     r5, #8
    blt     ConditionLoop_Start
    
ConditionLoop_End:
    ldr     r0, [r4, #BtlvMcss.btlvMcss_4C]
    bl      ARM9::Unk_2019B14
    mov     r0, #(BtlvMcss.field510 >> 4)
    lsl     r0, #4
    str     r0, [sp, #SP_FIELD_510]
    ldr     r0, [r4, r0]
    lsl     r0, #29
    lsr     r0, #31 ; get: 00000000_00000000_00000000_00000100
    bne     ReturnLink
    mov     r5, #0
    cmp     r5, #8
    blt     FieldPokeLoop_Setup
    
ReturnLink:
    b       Return
    
FieldPokeLoop_Setup:
    ldr     r0, [sp, #SP_FIELD_510]
    mov     r7, r4
    add     r0, #69
    str     r0, [sp, #SP_ONE_THIRD]
    
    ldr     r0, [sp, #SP_FIELD_510]
    add     r7, #84
    add     r0, #24
    str     r0, [sp, #SP_FIELD_510]
    
    ldr     r0, =0xFFFFFFFEFF
    asr     r0, #9 ; 11111111_11111111_11111111_11111111
    str     r0, [sp, #SP_VAR_28]
    
    mov     r0, #2
    str     r0, [sp, #SP_TWO]
    
    mov     r0, #0x80
    str     r0, [sp, #SP_BIT_7]
    
FieldPokeLoop_Start:
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Mcss_HasDataFile
    cmp     r0, #0
    beq     FieldPokeLoop_Skip1
    
    bl      ARM9::GFL_RandomMT
    mov     r1, #0
    mov     r2, #100
    mov     r3, #0
    blx     ARM9::Multiply64
    cmp     r1, #0
    bne     FieldPokeLoop_Skip1
    
    mov     r0, r5
    bl      BattleLevel::PowerOfTwo
    ldr     r1, [sp, #SP_FIELD_510]
    ldr     r1, [r4, r1]
    tst     r0, r1
    bne     FieldPokeLoop_Skip1
    
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Unk_21E7078
    cmp     r0, #0
    bne     FieldPokeLoop_Skip1
    
    mov     r0, #1
    str     r0, [sp, #SP_VAR_40]
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #2
    mov     r3, #4
    bl      BattleLevel::Unk_21E7638
    
FieldPokeLoop_Skip1:
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Mcss_HasDataFile
    cmp     r0, #0
    beq     FieldPokeLoop_CheckContinueLink
    
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Mcss_GetDataIndex
    str     r0, [sp, #SP_DATA_IDX]
    mov     r0, #0
    str     r0, [sp, #SP_IS_FADING_OUT]
    bl      BattleLevel::ViewCmd_WaitEffect
    cmp     r0, #0
    beq     FieldPokeLoop_Skip2
    
    ldr     r1, [sp, #SP_DATA_IDX]
    mov     r0, #0x5C
    mul     r0, r1
    ldr     r2, [r7, r0]
    lsl     r1, r2, #23
    lsr     r1, #31
    beq     FieldPokeLoop_Skip2
    
    ldr     r1, =0xFFFFFEFF
    and     r2, r1
    mov     r1, #0x80
    orr     r1, r2
    str     r1, [r7, r0]
    
FieldPokeLoop_Skip2:
    bl      BattleLevel::ViewCmd_WaitEffect
    cmp     r0, #0
    beq     FieldPokeLoop_Skip3
    
    ldr     r1, [sp, #SP_DATA_IDX]
    mov     r0, #0x5C
    mul     r0, r1
    add     r0, r4
    ldr     r0, [r0, #(BtlvMcss.mcssData + BtlvMcssData.arr4C)]
    lsl     r0, #24
    lsr     r0, #31
    bne     FieldPokeLoop_CheckContinueLink
    
FieldPokeLoop_Skip3:
    ldr     r1, [sp, #SP_DATA_IDX]
    mov     r0, #0x5C
    mov     r6, r1
    mul     r6, r0
    ldr     r1, [r7, r6]
    ldr     r0, [sp, #SP_BIT_7]
    bic     r1, r0
    str     r1, [r7, r6]
    
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Unk_21E7D18
    mov     r1, #4
    tst     r0, r1
    bne     FieldPokeLoop_CheckContinueLink
    
    mov     r0, r5
    add     r1, sp, #SP_VAR_1C
    add     r2, sp, #SP_DISPLAY_CONDITION
    bl      BattleLevel::Unk_21DFC34
    cmp     r0, #0
    bne     FieldPokeLoop_Skip4
    
FieldPokeLoop_CheckContinueLink:
    b       FieldPokeLoop_CheckContinue
    
FieldPokeLoop_Skip4:
    ldr     r0, [r7, r6]
    lsl     r0, #30
    lsr     r0, #31
    cmp     r0, #1
    bne     FieldPokeLoop_Skip7
    
    add     r3, r4, r6
    ldr     r0, [r3, #0x5C]
    cmp     r0, #0
    bne     FieldPokeLoop_Skip5
    
    mov     r0, #1
    str     r0, [r3, #0x5C]
    
FieldPokeLoop_Skip5:
    add     r2, r4, r6
    ldr     r1, [r2, #0x60]
    add     r0, r1, #1
    str     r0, [r2, #0x60]
    cmp     r1, #24
    ble     FieldPokeLoop_Skip7
    
    mov     r0, #1
    str     r0, [sp, #SP_IS_FADING_OUT]
    
    mov     r0, #0
    str     r0, [r2, #0x60]
    
    ldr     r1, [r2, #0x58]
    ldr     r0, [r2, #0x5C]
    add     r0, r1
    str     r0, [r2, #0x58]
    beq     FieldPokeLoop_Skip6
    
    cmp     r0, #12
    bne     FieldPokeLoop_Skip7
    
FieldPokeLoop_Skip6:
    ldr     r1, [r3, #0x5C]
    ldr     r0, [sp, #SP_VAR_28]
    mul     r0, r1
    str     r0, [r3, #0x5C]
    
FieldPokeLoop_Skip7:
    ldr     r0, [sp, #SP_DISPLAY_CONDITION]
    cmp     r0, #8
    bhi     Condition_None
    
    #SWITCH r0
    #CASE Condition_None
    #CASE Condition_Paralysis
    #CASE Condition_Frostbite
    #CASE Condition_Sleep
    #CASE Condition_Poison
    #CASE Condition_Burn
    #CASE Condition_None ; Confusion
    #CASE Condition_Poison ; Bad Poison
    #CASE Condition_None ; Infatuation
    
    
Condition_Sleep:
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Unk_21E7BEC
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #3
    bl      BattleLevel::Unk_21E7000
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #3
    bl      BattleLevel::Unk_21E70A4
    
    ldr     r2, [sp, #SP_ONE_THIRD] ; 1365 (1/3)
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Unk_21E7260
    b       Condition_Common
    
Condition_Paralysis:
    ldr     r0, [sp, #SP_IS_FADING_OUT]
    cmp     r0, #1
    bne     Condition_CommonColor
    
    ldr     r3, =CONDITION_COLOR_PARALYSIS
    b       Condition_SetColor
    
Condition_Frostbite:
    ldr     r3, =CONDITION_COLOR_FROSTBITE
    b       Condition_CheckIsFading
    
Condition_Poison:
    ldr     r3, =CONDITION_COLOR_POISON
    b       Condition_CheckIsFading
    
Condition_Burn:
    mov     r3, #CONDITION_COLOR_BURN
    
Condition_CheckIsFading:
    ldr     r0, [sp, #SP_IS_FADING_OUT]
    cmp     r0, #1
    bne     Condition_CommonColor
    
Condition_SetColor:
    mov     r0, r4
    mov     r1, r5
    add     r2, r4, r6
    ldr     r2, [r2, #0x58]
    bl      BattleLevel::Mcss_SetPokeColor
    
Condition_CommonColor:
    mov     r0, r4
    mov     r1, r5
    mov     r2, #4
    bl      BattleLevel::Unk_21E7000
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #3
    bl      BattleLevel::Unk_21E70A4
    
Condition_Common:
    ldr     r1, [r7, r6]
    mov     r0, #2
    orr     r0, r1
    str     r0, [r7, r6]
    b       FieldPokeLoop_Skip8
        
    
Condition_None:
    ldr     r0, [r7, r6]
    lsl     r0, #30
    lsr     r0, #31
    cmp     r0, #1
    bne     FieldPokeLoop_Skip8
    
    mov     r0, r4
    mov     r1, r5
    bl      BattleLevel::Unk_21E7BEC
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #4
    bl      BattleLevel::Unk_21E7000
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, #3
    bl      BattleLevel::Unk_21E70A4
    
    ldr     r1, [r7, r6]
    ldr     r0, [sp, #SP_TWO]
    bic     r1, r0
    str     r1, [r7, r6]
    
    add     r0, r4, r6
    mov     r1, #0
    str     r1, [r0, #0x5C]
    str     r1, [r0, #0x58]
    str     r1, [r0, #0x60]
    
FieldPokeLoop_Skip8:
    ldr     r0, [sp, #SP_DISPLAY_CONDITION]
    cmp     r0, #8
    bne     FieldPokeLoop_CheckContinue
    
    ldr     r0, [sp, #SP_VAR_1C]
    sub     r0, #2
    cmp     r0, #1
    bhi     FieldPokeLoop_Skip9
    
    mov     r0, r4
    mov     r1, r5
    ldr     r2, [sp, #SP_ONE_THIRD]
    b       FieldPokeLoop_SkipA
    
FieldPokeLoop_Skip9:
    mov     r2, #1
    mov     r0, r4
    mov     r1, r5
    lsl     r2, #12
    
FieldPokeLoop_SkipA:
    bl      BattleLevel::Unk_21E7260
    
FieldPokeLoop_CheckContinue:
    add     r5, #1
    cmp     r5, #8
    bge     Return
    b       FieldPokeLoop_Start
    
Return:
    add     sp, #ADD_STACK_SIZE
    pop     {r3-r7, pc}