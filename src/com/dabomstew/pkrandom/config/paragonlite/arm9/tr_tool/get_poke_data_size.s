; r0    trainerDataPtr
; r1    *offsets

    push    {r4-r6, lr}
    mov     r4, r0
    mov     r5, r1
    
    #printf("    ARM9::TrTool_GetPokeDataSize (LR=0x%08X)", lr)
    
    ldrh    r6, [r4, #TrainerData.flags]
    #printf("        flags=0x%04X", r6)
    
    mov     r4, #TrainerPoke.BASE_SIZE
    
    cmp     r5, #0
    beq     CheckHasStatModifiers
    mov     r0, #0
    str     r0, [r5]
    
CheckHasStatModifiers:
    #printf("        check stat modifiers... (size=%d)", r4)
    mov     r0, #((1 << TrainerData_Flags.hasNatureBit) | (1 << TrainerData_Flags.hasIVsEVsBit))
    tst     r6, r0
    beq     CheckHasItem
    
    cmp     r5, #0 ; null
    beq     AddStatModifiers
    strb    r4, [r5, #0x00]
    
AddStatModifiers:
    add     r4, #TrainerPoke.STAT_MODIFIERS_SIZE
    #printf("        has stat modifiers (size=%d)", r4)


CheckHasItem:
    #printf("        check has item... (size=%d)", r4)
    mov     r0, #(1 << TrainerData_Flags.hasItemBit)
    tst     r6, r0
    beq     CheckHasMoves
    
    cmp     r5, #0 ; null
    beq     AddItem
    strb    r4, [r5, #0x01]
    
AddItem:
    add     r4, #TrainerPoke.ITEM_SIZE
    #printf("        has item (size=%d)", r4)
    
    
CheckHasMoves:
    #printf("        check has moves... (size=%d)", r4)
    mov     r0, #(1 << TrainerData_Flags.hasMovesBit)
    tst     r6, r0
    beq     PrintAndReturn
    
    cmp     r5, #0 ; null
    beq     AddMoves
    strb    r4, [r5, #0x02]
    
AddMoves:
    add     r4, #TrainerPoke.MOVES_SIZE
    #printf("        has moves (size=%d)", r4)
    
    
PrintAndReturn:
    #printf("        size=%d", r4)
    
#if DEBUG
TryPrintStatModifiersOffset:
    ldrb    r0, [r5, #0x00]
    cmp     r0, #0
    beq     TryPrintItemOffset
    #printf("        statModifiersOffset=0x%02X", r0)
    
TryPrintItemOffset:
    ldrb    r0, [r5, #0x01]
    cmp     r0, #0
    beq     TryPrintMovesOffset
    #printf("        itemOffset=0x%02X", r0)
    
TryPrintMovesOffset:
    ldrb    r0, [r5, #0x01]
    cmp     r0, #0
    beq     Return
    #printf("        movesOffset=0x%02X", r0)
#endif

Return:
    mov     r0, r4
    #printf("        offsets=0x%06X", r5)
    pop     {r4-r6, pc}