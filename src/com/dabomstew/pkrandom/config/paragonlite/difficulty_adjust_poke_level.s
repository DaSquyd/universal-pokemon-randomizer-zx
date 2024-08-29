#DEFINE BattleDataBlock 0x00
#DEFINE PartyIndex 0x04

#DEFINE __MAX__ 0x08

    push    {r3-r7, lr}
    sub     sp, #__MAX__
    str     r0, [sp, #BattleDataBlock]
    mov     r5, r2
    mov     r0, r1
    bl      ARM9::GetSaveInfoBase ; 02017934
    bl      ARM9::GetKeyInfoSaveBlock ; 020104A4
    bl      ARM9::GetDifficultyFromKey ; 02010528
    mov     r4, r0
    mov     r0, r5
    bl      ARM9::GetDifficultyLevelChange ; uses zone data to get a value (pretty much just 1-5 as far as I'm aware)
    mov     r7, r0
    
    mov     r1, #0x4F
    ldr     r0, [sp, #BattleDataBlock]
    mov     r2, #0
    lsl     r1, #2    ; r1 := 316 (offset)
    strb    r2, [r0, r1]
    
    cmp     r4, #0 ; Easy Mode
    bne     CheckNormalMode
    
IsEasyMode:
    sub     r0, r2, #1 ; r0 := -1
    mul     r7, r0 ; r7 := -r2
    
CheckNormalMode:
    cmp     r4, #1 ; Normal Mode
    beq     Return
    
    mov     r1, #0x4F
    ldr     r0, [sp, #BattleDataBlock]
    lsl     r1, #2    ; r1 := 316 (offset)
    strb    r7, [r0, r1]
    
    mov     r0, #0
    str     r0, [sp, #PartyIndex]
    
PartyLoopStart:
    ldr     r0, [sp, #PartyIndex]
    lsl     r1, r0, #2
    ldr     r0, =ARM9::Data_EnemyPartyIndices
    ldr     r0, [r0, r1]
    lsl     r1, r0, #2
    ldr     r0, [sp, #BattleDataBlock]
    add     r0, r1
    ldr     r5, [r0, #0x24] ; party block
    cmp     r5, #0
    beq     PartyLoopEnd
    
    mov     r0, r5
    mov     r4, #0
    bl      ARM9::GetPartySize
    cmp     r0, #0
    ble     PartyLoopEnd
    
PokeLoopStart:
    mov     r0, r5
    mov     r1, r4
    bl      ARM9::GetPartyPokeAddress
    mov     r6, r0
    mov     r1, #0x9E ; level
    mov     r2, #0
    bl      ARM9::GetPokeStat
    lsl     r0, #16
    lsr     r0, #16
    
    add     r2, r0, r7
    cmp     r2, #0
    bgt     SetLevel
    mov     r2, #1 ; ensures that level never goes below 1
    
SetLevel:
    mov     r0, r6
    mov     r1, #0x9E ; level
    bl      ARM9::SetPokeStat
    
    mov     r0, r6
    bl      ARM9::UpdateAllPokeStats
    
    mov     r0, r5
    add     r4, #1
    bl      ARM9::GetPartySize
    cmp     r4, r0
    blt     PokeLoopStart
    
PartyLoopEnd:
    ldr     r0, [sp, #PartyIndex]
    add     r0, #1
    str     r0, [sp, #PartyIndex]
    cmp     r0, #2
    bcc     PartyLoopStart
    
Return:
    add     sp, #__MAX__
    pop     {r3-r7, pc}