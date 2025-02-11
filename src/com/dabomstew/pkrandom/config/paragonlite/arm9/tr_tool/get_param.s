#define S_TrainerData 0x00
#define STACK_SIZE (Math.trunc((TrainerData.SIZE + 3) / 4) * 4)

; r0: trainerId
; r1: param

    push    {r3-r5, lr}
    sub     sp, #STACK_SIZE
    mov     r5, r1 ; param
    mov     r1, #(TR_NULL - 1)
    cmp     r5, r1
    bhi     Return
    
    add     r4, sp, #S_TrainerData
    mov     r1, r4
    bl      ARM9::LoadTrainerFile
    
    #switch r0 r5
    #case Flags
    #case Class
    #case BattleStyle
    #case PartySize
    #case Item
    #case AIFlags
    #case IsHealer
    #case PayoutScale
    #case PrizeItem
    #case HasMoves
    #case HasItems
    #case HasNature
    #case HasIVsEVs
    #case IsPooled
    #case PokeCount
    
    
Flags:
    ldrb    r0, [r4, #TrainerData.flags]
    b       Return
    
    
Class:
    ldrb    r0, [r4, #TrainerData.class]
    b       Return
    
    
BattleStyle:
    ldrb    r0, [r4, #TrainerData.flags]
    mov     r1, #TrainerData_Flags.battleStyleMask
    and     r0, r1
    b       Return
    
    
PartySize:
    ldrb    r0, [r4, #TrainerData.partySize]
    b       Return
    
    
Item:
    sub     r0, r5, #TR_Item1
    lsl     r1, r0, #1
    add     r0, sp, #(S_TrainerData + TrainerData.items[0])
    ldrh    r0, [r0, r1]
    b       Return
    
    
AIFlags:
    ldr     r0, [sp, #TrainerData.aiFlags]
    b       Return
    
    
IsHealer:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.isHealerBit + TrainerData_Flags.isHealerSize))
    lsr     r0, #(32 - TrainerData_Flags.isHealerSize)
    b       Return
    
    
PayoutScale:
    ldrb    r0, [r4, #TrainerData.payoutScale]
    b       Return
    
    
PrizeItem:
    ldrh    r0, [r4, #TrainerData.prizeItem]
    b       Return
    
    
HasMoves:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.hasMovesBit + TrainerData_Flags.hasMovesSize))
    lsr     r0, #(32 - TrainerData_Flags.hasMovesSize)
    b       Return
    
    
HasItems:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.hasItemsBit + TrainerData_Flags.hasItemsSize))
    lsr     r0, #(32 - TrainerData_Flags.hasItemsSize)
    b       Return
    
    
HasNature:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.hasNatureBit + TrainerData_Flags.hasNatureSize))
    lsr     r0, #(32 - TrainerData_Flags.hasNatureSize)
    b       Return
    
    
HasIVsEVs:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.hasIVsEVsBit + TrainerData_Flags.hasIVsEVsSize))
    lsr     r0, #(32 - TrainerData_Flags.hasIVsEVsSize)
    b       Return
    
    
IsPooled:
    ldrb    r0, [r4, #TrainerData.flags]
    lsl     r0, #(32 - (TrainerData_Flags.isPooledBit + TrainerData_Flags.isPooledSize))
    lsr     r0, #(32 - TrainerData_Flags.isPooledSize)
    b       Return
    
    
PokeCount:
    ldrb    r0, [r4, #TrainerData.pokeCount]
    
    
Return:
    add     sp, #STACK_SIZE
    pop     {r3-r5, pc}