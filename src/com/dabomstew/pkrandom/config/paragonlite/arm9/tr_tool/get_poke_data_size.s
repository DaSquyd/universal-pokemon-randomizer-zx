; r0: trainerDataPtr

    ldrh    r1, [r0, #TrainerData.flags]
    mov     r0, #TrainerPoke.BASE_SIZE
    
CheckHasMoves:
    mov     r2, #(1 << TrainerData_Flags.hasMovesBit)
    tst     r1, r2
    beq     CheckHasItem
    add     r0, #TrainerPoke.MOVES_SIZE

CheckHasItem:
    mov     r2, #(1 << TrainerData_Flags.hasItemsBit)
    tst     r1, r2
    beq     CheckHasStatModifiers
    add     r0, #TrainerPoke.ITEM_SIZE
    
CheckHasStatModifiers:
    mov     r2, #((1 << TrainerData_Flags.hasNatureBit) | (1 << TrainerData_Flags.hasIVsEVsBit))
    tst     r1, r2
    beq     Return
    add     r0, #TrainerPoke.STAT_MODIFIERS_SIZE
    
Return:
    bx      lr