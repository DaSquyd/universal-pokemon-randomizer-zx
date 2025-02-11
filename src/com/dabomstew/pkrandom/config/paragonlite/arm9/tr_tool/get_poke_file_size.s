; r0: trainerDataPtr

    push    {r4, lr}
    mov     r4, r0
    
    bl      ARM9::Trainer_GetPokeDataSize
    
    ldrb    r1, [r4, #TrainerData.pokeCount]
    mul     r0, r1
    
    ; check is pooled
    ldrb    r1, [r4, #TrainerData.flags]
    mov     r2, #(1 << TrainerData_Flags.isPooled)
    tst     r1, r2
    beq     Return
    
    ; is pooled (add header)
    add     r0, #TrainerPoke_Header.SIZE
    
Return:
    pop     {r4, pc}