; r0: trainerDataPtr

    push    {r4, lr}
    mov     r4, r0
    
    bl      ARM9::TrTool_GetPokeDataSize
    
    ldrb    r1, [r4, #TrainerData.pokeCount]
    mul     r0, r1
    
    ; check is pooled
    ldrh    r1, [r4, #TrainerData.flags]
    mov     r2, #1
    lsl     r2, #TrainerData_Flags.isPooledBit
    tst     r1, r2
    beq     Return
    
    ; is pooled (add header)
    add     r0, #TrainerPoke_Header.SIZE
    
Return:
    pop     {r4, pc}