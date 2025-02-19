#define S_ArgHeapLowId 0x00

#define S_GameSystemWork 0x04


    push    {r3-r7, lr}
    sub     sp, #0x08
    mov     r4, r0 ; pwtSys
    mov     r5, r2 ; pwtTrainer
    str     r1, [sp, #S_GameSystemWork]
    
    ldrh    r2, [r4, #0x00] ; heapId
    ldr     r6, =0x7FFF
    and     r2, r6
    add     r6, #1
    orr     r6, r2 ; r6 := heapLowId
    bl      Script_PWT::GetMaxPartySize
    mov     r7, r0
    
    ldrb    r0, [r5, #0x00]
    lsl     r0, #29
    lsr     r0, #29
    cmp     r0, #3
    bhi     Return
    
    #switch r0 
    #case Rom
    #case Download ; download
    #case Download ; rom + download
    #case Return
    
    
Rom:
    ldrh    r0, [r5, #0x06]
    #read_bits(r0, 0, 12)
    cmp     r0, #145 ; rival trainer class
    bne     Rom_OtherTrainer
    
Rom_Rival:
    ldr     r0, [sp, #S_GameSystemWork]
    bl      PWT_Battle::GetRivalTrainerId
    mov     r1, r0
    ldr     r0, [sp, #S_GameSystemWork] ; This part is new!
    mov     r2, #(0x13C0 >> 6)
    lsl     r2, #6
    ldr     r2, [r4, r2]
    mov     r3, r6
    bl      ARM9::TrTool_LoadParty_Core
    
    add     sp, #0x08
    pop     {r3-r7, pc}
    
    
Rom_OtherTrainer:
    mov     r0, r4
    bl      Script_PWT::GetTournamentId
    mov     r2, r0 ; arg2 = tournamentId
    mov     r0, #(0x13C0 >> 6)
    lsl     r0, #6
    ldr     r0, [r4, r0] ; arg0 = pwtSys->trainerParty
    mov     r1, r5 ; arg1 = pwtTrainer
    mov     r3, r7 ; arg3 = maxPartySize
    str     r6, [sp, #S_ArgHeapLowId]
    bl      PWT_Battle::SetRomParty
    
    add     sp, #0x08
    pop     {r3-r7, pc}
    
    
Download:
    mov     r0, #(0x13C0 >> 6)
    lsl     r0, #6
    ldr     r0, [r4, r0] ; arg0 = pwtSys->trainerParty
    mov     r1, #(0x0194 >> 2)
    lsl     r1, #2
    add     r1, r4 ; arg1 = &(pwtSys->downloadData)
    ldrb    r2, [r5, #0x03]
    #read_bits(r2, 6, 2) ; arg2 = pwtTrainer->downloadTrainerId
    mov     r3, r7 ; arg3 = maxPartySize
    str     r6, [sp, #S_ArgHeapLowId] ; arg4 = heapLowId
    bl      PWT_Battle::SetDownloadedParty
    

Return:
    add     sp, #0x08
    pop     {r3-r7, pc}
    