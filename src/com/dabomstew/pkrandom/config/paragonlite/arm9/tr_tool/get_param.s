#define S_TrainerData 0x00
#define STACK_SIZE (Math.floor((TrainerData.SIZE + 3) / 4) * 4)

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
    bl      ARM9::TrTool_LoadTrainerData
    
    #switch r0 r5
    #case Flags
    #case Class
    #case BattleStyle
    #case PartySize
    #case Item
    #case AIFlags
    #case IsHealer
    #case PayoutScale
    #case RewardItem
    #case HasMoves
    #case HasItems
    #case HasNature
    #case HasIVsEVs
    #case HasMaxPP
    #case IsPooled
    #case PokeCount
    
    
Flags:
    ldrh    r0, [r4, #TrainerData.flags]
    b       Return
    
    
Class:
    ldrb    r0, [r4, #TrainerData.class]
    b       Return
    
    
BattleStyle:
    ldrh    r0, [r4, #TrainerData.flags]
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
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isHealerBit, 1)
    b       Return
    
    
PayoutScale:
    ldrb    r0, [r4, #TrainerData.payoutScale]
    b       Return
    
    
RewardItem:
    ldrh    r0, [r4, #TrainerData.rewardItem]
    b       Return
    
    
HasMoves:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMovesBit, 1)
    b       Return
    
    
HasItems:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasItemsBit, 1)
    b       Return
    
    
HasNature:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasNatureBit, 1)
    b       Return
    
    
HasIVsEVs:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasIVsEVsBit, 1)
    b       Return
    
    
HasMaxPP:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMaxPPBit, 1)
    b       Return
    
    
IsPooled:
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isPooledBit, 1)
    b       Return
    
    
PokeCount:
    ldrb    r0, [r4, #TrainerData.pokeCount]
    
    
Return:
    add     sp, #STACK_SIZE
    pop     {r3-r5, pc}