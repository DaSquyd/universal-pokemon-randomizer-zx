#define S_TrainerData 0x00
#define STACK_SIZE (Math.floor((TrainerData.SIZE + 3) / 4) * 4)

; r0: trainerId
; r1: param

    push    {r3-r6, lr}
    sub     sp, #STACK_SIZE
    mov     r6, r0 ; trainerId
    mov     r5, r1 ; param
    
    #printf("ARM9::TrTool_GetParam (LR=0x%08X)", lr)
    
    mov     r1, #(TR_NULL - 1)
    cmp     r5, r1
    
#if DEBUG
    bls     LoadTrainerData
    b       Return
#else
    bhi     Return
#endif
    
LoadTrainerData:
    mov     r0, r6
    add     r4, sp, #S_TrainerData
    mov     r1, r4
    bl      ARM9::TrTool_LoadTrainerData
    
    #switch r0 r5
    #case Flags
    #case Class
    #case BattleStyle
    #case PartySize
    #case Item
    #case Item
    #case Item
    #case Item
    #case AIFlags
    #case IsHealer
    #case PayoutScale
    #case RewardItem
    #case HasMoves
    #case HasItem
    #case HasNature
    #case HasIVsEVs
    #case HasMaxPP
    #case IsPooled
    #case PokeCount
    
    
Flags:
    #printf("Got flags: 0x%04X", ldrh [r4, #TrainerData.flags])
    ldrh    r0, [r4, #TrainerData.flags]
    b       Return
    
    
Class:
    #printf("Got class: %d", ldrb [r4, #TrainerData.class])
    ldrb    r0, [r4, #TrainerData.class]
    b       Return
    
    
BattleStyle:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.battleStyleBit, #TrainerData_Flags.battleStyleSize)
    #printf("Got battle style: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.battleStyleBit, #TrainerData_Flags.battleStyleSize)
    b       Return
    
    
PartySize:
    #printf("Got party size: %d", ldrb [r4, #TrainerData.partySize])
    ldrb    r0, [r4, #TrainerData.partySize]
    b       Return
    
    
Item:
#if DEBUG
    sub     r0, r5, #TR_Item1
    lsl     r1, r0, #1
    add     r0, sp, #(S_TrainerData + TrainerData.items[0])
    #printf("Got item: %d", ldrh [r0, r1])
#endif
    sub     r0, r5, #TR_Item1
    lsl     r1, r0, #1
    add     r0, sp, #(S_TrainerData + TrainerData.items[0])
    ldrh    r0, [r0, r1]
    b       Return
    
    
AIFlags:
    #printf("Got AI flags: 0x%08X", ldr [sp, #TrainerData.aiFlags])
    ldr     r0, [sp, #TrainerData.aiFlags]
    b       Return
    
    
IsHealer:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isHealerBit, 1)
    #printf("Got is healer: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isHealerBit, 1)
    b       Return
    
    
PayoutScale:
    #printf("Got payout scale: %d", ldrb [r4, #TrainerData.payoutScale])
    ldrb    r0, [r4, #TrainerData.payoutScale]
    b       Return
    
    
RewardItem:
    #printf("Got reward item: %d", ldrh [r4, #TrainerData.rewardItem])
    ldrh    r0, [r4, #TrainerData.rewardItem]
    b       Return
    
    
HasMoves:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMovesBit, 1)
    #printf("Got has moves: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMovesBit, 1)
    b       Return
    
    
HasItem:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasItemBit, 1)
    #printf("Got has item: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasItemBit, 1)
    b       Return
    
    
HasNature:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasNatureBit, 1)
    #printf("Got has nature: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasNatureBit, 1)
    b       Return
    
    
HasIVsEVs:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasIVsEVsBit, 1)
    #printf("Got has IVs and EVs: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasIVsEVsBit, 1)
    b       Return
    
    
HasMaxPP:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMaxPPBit, 1)
    #printf("Got has max PP: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasMaxPPBit, 1)
    b       Return
    
    
IsPooled:
#if DEBUG
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isPooledBit, 1)
    #printf("Got is pooled: %d", r0)
#endif
    ldrh    r0, [r4, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.isPooledBit, 1)
    b       Return
    
    
PokeCount:
    #printf("Got poke count: %d", ldrb [r4, #TrainerData.pokeCount])
    ldrb    r0, [r4, #TrainerData.pokeCount]
    
    
Return:
    add     sp, #STACK_SIZE
    pop     {r3-r6, pc}