; r0: trainerDataPtr

    push    {r4-r5, lr}
    mov     r4, r0
    
#if DEBUG
    #printf("ARM9::TrTool_GetPokeFileSize (LR=0x%08X)", lr)
    #printf("    pokeCount=%d", ldrb [r4, #TrainerData.pokeCount])
    mov     r0, r4
#endif
    
    bl      ARM9::TrTool_GetPokeDataSize
    mov     r5, r0
    ldrb    r0, [r4, #TrainerData.pokeCount]
    mul     r5, r0
    #printf("    size=%d", r5)
    
    ; check is pooled
    #printf("    check is pooled")
    ldrh    r1, [r4, #TrainerData.flags]
    mov     r2, #1
    lsl     r2, #TrainerData_Flags.isPooledBit
    tst     r1, r2
    beq     Return
    
    ; is pooled (add header)
    add     r5, #TrainerPoke_Header.SIZE
    #printf("    is pooled (size=%d)", r5)
    
Return:
    #printf("    final size = %d", r5)
    mov     r0, r5
    pop     {r4-r5, pc}