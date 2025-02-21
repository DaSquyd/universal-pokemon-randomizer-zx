; r0: trainerDataPtr

    push    {r4-r5, lr}
    mov     r4, r0
    
    #printf("    ARM9::TrTool_GetPokeDataSize (LR=0x%08X)", lr)
    
    ldrh    r5, [r4, #TrainerData.flags]
    #printf("        flags=0x%04X", r5)
    
    mov     r4, #TrainerPoke.BASE_SIZE
    
CheckHasMoves:
    #printf("        check has moves... (size=%d)", r4)
    mov     r0, #(1 << TrainerData_Flags.hasMovesBit)
    tst     r5, r0
    beq     CheckHasItem
    add     r4, #TrainerPoke.MOVES_SIZE
    #printf("        has moves (size=%d)", r4)

CheckHasItem:
    #printf("        check has item... (size=%d)", r4)
    mov     r0, #(1 << TrainerData_Flags.hasItemBit)
    tst     r5, r0
    beq     CheckHasStatModifiers
    add     r4, #TrainerPoke.ITEM_SIZE
    #printf("        has item (size=%d)", r4)
    
CheckHasStatModifiers:
    #printf("        check stat modifiers... (size=%d)", r4)
    mov     r0, #((1 << TrainerData_Flags.hasNatureBit) | (1 << TrainerData_Flags.hasIVsEVsBit))
    tst     r5, r0
    beq     Return
    add     r4, #TrainerPoke.STAT_MODIFIERS_SIZE
    #printf("        has stat modifiers (size=%d)", r4)
    
Return:
    #printf("        final size = %d", r4)
    mov     r0, r4
    pop     {r4-r5, pc}