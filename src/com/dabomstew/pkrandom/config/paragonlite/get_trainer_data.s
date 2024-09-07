#DEFINE FILE_SIZE 0x18 ; 0x16 actual size, but this must be 4-byte-aligned

    push    {r3-r6, lr}
    sub     sp, #FILE_SIZE
    mov     r5, r1
    mov     r1, sp
    mov     r6, sp
    bl      ARM9::LoadTrainerFile
    cmp     r5, #13
    bhi     Return
    
    #SWITCH r5
    #CASE Flags
    #CASE Class
    #CASE BattleType
    #CASE PartySize
    #CASE Item
    #CASE Item
    #CASE Item
    #CASE Item
    #CASE AIFlags
    #CASE IsHealer
    #CASE RewardMoneyScale
    #CASE RewardItem
    #CASE ChannelCounts
    #CASE PokeCount
    
Flags:
    ldrb    r0, [r6, #0x00]
    b       Return
    
Class:
    ldrb    r0, [r6, #0x01]
    b       Return
    
BattleType:
    ldrb    r0, [r6, #0x02]
    b       Return
    
PartySize:
    b       PokeCount ; TODO: Remove this temp jump
    
    ldrh    r2, [r6, #0x14]
    
    ; A (0-6)
    lsl     r0, r2, #29
    lsr     r0, #29
    
    ; B (0-5)
    lsl     r1, r2, #26
    lsr     r1, #29
    orr     r0, r1
    
    ; C (0-4)
    lsl     r1, r2, #23
    lsr     r1, #29
    orr     r0, r1
    
    ; D (0-3)
    lsl     r1, r2, #21
    lsr     r1, #30
    orr     r0, r1
    
    ; E (0-2)
    lsl     r1, r2, #19
    lsr     r1, #30
    orr     r0, r1
    
    ; F (0-1)
    lsl     r1, r2, #18
    lsr     r1, #31
    orr     r0, r1
    
    b       Return
    
Item:
    sub     r0, r5, #4
    lsl     r1, r0, #1
    add     r0, sp, #4
    ldrh    r0, [r0, r1]
    b       Return
    
AIFlags:
    ldr     r0, [sp, #0x0C]
    b       Return
    
IsHealer:
    ldrb    r0, [r6, #0x10]
    lsl     r0, #31
    lsr     r0, #31
    b       Return
    
RewardMoneyScale:
    ldrb    r0, [r6, #0x11]
    b       Return
    
RewardItem:
    ldrh    r0, [r6, #0x12]
    b       Return
    
ChannelCounts:
    ldrh    r0, [r6, #0x14]
    b       Return
    
PokeCount:
    ldrb    r0, [r6, #0x03]

Return:
    add     sp, #FILE_SIZE
    pop     {r3-r6, pc}