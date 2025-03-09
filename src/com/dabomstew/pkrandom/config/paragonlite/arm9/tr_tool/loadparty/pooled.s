; r0    playerId
; r1    trainerId
; r2    partyPtr
; r3    heapId
; arg0  trainerDataPtr
; arg4  trainerPokePtr
; arg8  tempPokePtr
; argC  pokeDataSize
; arg10 pokeDataOffsets

; Handles randomization from trainer poke pools

; allocate args
#define S_ArgLineNum 0x00

; Trainer_MakePoke args
#define S_ArgHeapId 0x00
#define S_ArgPokeDataOffsets 0x04 ; [0] = statModifiers, [1] = item, [2] = moves
#define S_ArgTeamRand 0x08
#define S_ArgTrainerDataPtr 0x0C
#define S_ArgTempPokePtr 0x10

#define S_PlayerId 0x14
#define S_TrainerId 0x18
#define S_PartyPtr 0x1C
#define S_HeaderFlags 0x20
#define S_SlotsRemaining 0x24 ; 6 bytes, 1 byte for remaining (8), used for randomness to create balanced likelihoods
#define S_SlotsRemainingCount 0x2A
#define S_PoolOffsets 0x2C ; 12 bytes
#define S_CurrentSlotId 0x38
#define S_TempPool 0x3C ; 30 bytes, 1 byte for count (32), 
#define S_TempPoolCount 0x5A
#define S_CurrentPoolId 0x5C ; 2 bytes
#define S_CurrentPoolOffset 0x5E ; 2 bytes
#define S_SelectedPokeIndices 0x60 ; 6 bytes (8)
#define S_UsedSpecies 0x68 ; 12 bytes
#define S_UsedItems 0x74 ; 12 bytes
#define STACK_SIZE 0x80

#define PUSH_SIZE (5 * 4) ; r4-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_TrainerDataPtr (ARG_OFFSET + 0x00)
#define ARG_TrainerPokePtr (ARG_OFFSET + 0x04)
#define ARG_TempPokePtr (ARG_OFFSET + 0x08)
#define ARG_PokeDataSize (ARG_OFFSET + 0x0C)
#define ARG_PokeDataOffsets (ARG_OFFSET + 0x10)

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    
    ; playerId = inPlayerId;
    str     r0, [sp, #S_PlayerId]
    
    ; trainerId = inTrainerId;
    str     r1, [sp, #S_TrainerId]
    
    ; partyPtr = inPartyPtr;
    str     r2, [sp, #S_PartyPtr]
    
    ; argHeapId = inHeapId;
    str     r3, [sp, #S_ArgHeapId]
    
    ; argPokeDataOffsets = inPokeDataOffsets;
    ldr     r0, [sp, #ARG_PokeDataOffsets]
    str     r0, [sp, #S_ArgPokeDataOffsets]
    
    ; argTeamRand = 0; // TODO: make this actually... random somehow?
    mov     r0, #0
    str     r0, [sp, #S_ArgTeamRand]
    
    ; argTrainerDataPtr = trainerDataPtr;
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    str     r0, [sp, #S_ArgTrainerDataPtr]
    
    ; argTempPokePtr = tempPokePtr;
    ldr     r0, [sp, #ARG_TempPokePtr]
    str     r0, [sp, #S_ArgTempPokePtr]
    
    #printf("ARM9::TrTool_LoadParty_Pooled (LR=0x%08X)", lr)
    
    ; headerFlags = trainerPokePtr.header.flags;
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    ldrh    r0, [r0, #TrainerPoke_Header.flags]
    str     r0, [sp, #S_HeaderFlags]


PoolSizeLoop_Setup:
    #printf("    pool size loop")
    mov     r4, #0 ; current pool id
    mov     r5, #TrainerPoke_Header.SIZE ; offset (starting position)
    
    ; store pool sizes for later use
PoolSizeLoop_Start:
#if DEBUG
    mov     r0, r4
    add     r0, #65 ; 'A'
    #printf("        pool%cOffset=%d", r0, r5)
#endif
    
    ; poolOffsets[i] = poolOffset;
    add     r0, sp, #S_PoolOffsets
    lsl     r1, r4, #1
    strh    r5, [r0, r1]
    
    ; poolOffset += trainerPokePtr->poolSizes[i] * pokeDataSize;
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    add     r0, #TrainerPoke_Header.poolSizes[0]
    ldrb    r0, [r0, r4] ; num pokes in pool
    ldr     r1, [sp, #ARG_PokeDataSize] ; size
    mul     r0, r1 ; size of current pool in bytes
    add     r5, r0
    
PoolSizeLoop_End:
    add     r4, #1
    cmp     r4, #6 ; pools count
    bcc     PoolSizeLoop_Start
    

; build slotsRemaining
SlotsRemainingLoop_Setup:
    #printf("    slots remaining loop")
    mov     r4, #0 ; iterator (slot id)
    add     r0, sp, #S_SlotsRemaining
    strb    r4, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)]

SlotsRemainingLoop_Start:
    ; TrainerPoke_Header_Slot slot = trainerPokePtr->header.slots[i];
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    lsl     r1, r4, #1
    ldrh    r5, [r0, r1] ; r5 := current slot data
    
    ; if (slot.level == 0)
    ;     continue;
    #read_bits(r0, r5, TrainerPoke_Header_Slot.levelBit, TrainerPoke_Header_Slot.levelSize) ; r0 := level
    cmp     r0, #0
    beq     SlotsRemainingLoop_End
    
    ; slotsRemaining[slotsRemainingCount] = i;
    add     r0, sp, #S_SlotsRemaining
    ldrb    r1, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)] ; r1 := count
    strb    r4, [r0, r1]
    
    ; slotsRemainingCount += 1;
    add     r1, #1
    strb    r1, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)]

SlotsRemainingLoop_End:
    add     r4, #1
    cmp     r4, #6 ; max slots
    bcc     SlotsRemainingLoop_Start

    
    ; iterate through each slot and select
SlotLoop_Setup:
    #printf("    slot loop")
    mov     r4, #0 ; current slot index (not slot id)
    
SlotLoop_Start:
    #printf("        slotIteration=%d", r4)
    
    ; int rand = (((long) ARM9::GFL_RandomMT()) * slotsRemainingCount) >> 32;
    bl      ARM9::GFL_RandomMT
    mov     r1, #0
    add     r2, sp, #S_SlotsRemaining
    ldrb    r2, [r2, #(S_SlotsRemainingCount - S_SlotsRemaining)]
    mov     r3, #0
    blx     ARM9::Multiply64 ; we use r1 as the result (higher 32 bits)
    
    ; int currentSlotId = slotsRemaining[rand];
    add     r0, sp, #S_SlotsRemaining
    ldrb    r5, [r0, r1]
    str     r5, [sp, #S_CurrentSlotId]
    
    ; slotsRemainingCount -= 1;
    ldrb    r2, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)]
    sub     r2, #1
    strb    r2, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)]
    
    ; slotsRemaining[rand] = slotsRemaining[slotsRemainingCount];
    ldrb    r2, [r0, r2]
    strb    r2, [r0, r1]
    
    #printf("        selectedSlot=%d...", r5)

    ; TrainerPoke_Header_Slot slot = trainerPokePtr->header.slots[currentSlotId];
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    lsl     r1, r5, #1
    ldrh    r5, [r0, r1]
    
    ; currentPoolId = slot.poolId;
    #read_bits(r0, r5, TrainerPoke_Header_Slot.poolBit, TrainerPoke_Header_Slot.poolSize)
    add     r1, sp, #S_CurrentPoolId
    strh    r0, [r1]
    
    #printf("        currentPoolId=%d", r0)
    
    ; currentPoolOffset = poolOffsets[currentPoolId];
    lsl     r0, #1
    add     r1, sp, #S_PoolOffsets
    ldrh    r0, [r1, r0]
    add     r1, sp, #S_CurrentPoolId
    strh    r0, [r1, #(S_CurrentPoolOffset - S_CurrentPoolId)]
    #printf("        currentPoolOffset=%d", r0)
    
#if DEBUG
    #read_bits(r0, r5, TrainerPoke_Header_Slot.poolBit, TrainerPoke_Header_Slot.poolSize)
    mov     r1, r0
    add     r1, #65 ; 'A'
    #printf("        slot.pool='%c' (%d)", r1, r0)
    
    #read_bits(r0, r5, TrainerPoke_Header_Slot.ivBit, TrainerPoke_Header_Slot.ivSize)
    #printf("        slot.iv=%d", r0)
    
    #read_bits(r0, r5, TrainerPoke_Header_Slot.levelBit, TrainerPoke_Header_Slot.levelSize)
    #printf("        slot.level=%d", r0)
#endif


SlotLoop_MakeTempPoolLoop_Setup:
    #printf("        make temp pool loop", r0)
    mov     r7, #0
    
	; tempPoolCount = 0;
    add     r0, sp, #S_TempPool
    strb    r7, [r0, #(S_TempPoolCount - S_TempPool)]
    
    ; loop through the pool and add allowed ids into tempPool
SlotLoop_MakeTempPoolLoop_Start:
    #printf("            %d...", r7)
    
    ; TrainerPoke* currentPoolPokePtr = currentPoolOffset + pokeDataSize * j;
    ldr     r0, [sp, #ARG_PokeDataSize]
    mul     r0, r7
    add     r1, sp, #S_CurrentPoolId
    ldrh    r1, [r1, #(S_CurrentPoolOffset - S_CurrentPoolId)]
    add     r0, r1
    ldr     r1, [sp, #ARG_TrainerPokePtr]
    add     r6, r0, r1 ; r6 := currentPokePtr
    
    #printf("            flags=0x%04X", ldrh [r0, #TrainerPoke_Header.flags])
    
SlotLoop_MakeTempPoolLoop_CheckUniqueSpecies:
    ; if ((trainerPokePtr->header.flags & (1 << TrainerPoke_Header_Flags.uniqueSpeciesBit)) != 0)
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    ldrh    r0, [r0, #TrainerPoke_Header.flags]
    mov     r1, #(1 << TrainerPoke_Header_Flags.uniqueSpeciesBit)
    tst     r0, r1
    beq     SlotLoop_MakeTempPoolLoop_CheckUniqueItems
    
    #printf("            enforcing unique species...")
    ; if (ARM9::ContainsHalfword(&usedSpecies, i, currentPoolPokePtr->species))
    ;     continue;
    add     r0, sp, #S_UsedSpecies
    mov     r1, r4
    ldrh    r2, [r6, #TrainerPoke.species]
    bl      ARM9::ContainsHalfword
    cmp     r0, #FALSE
    bne     SlotLoop_MakeTempPoolLoop_End

SlotLoop_MakeTempPoolLoop_CheckUniqueItems:
    ; if ((trainerPokePtr->header.flags & (1 << TrainerPoke_Header_Flags.uniqueItemsBit)) != 0)
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    ldrh    r0, [r0, #TrainerPoke_Header.flags]
    mov     r1, #(1 << TrainerPoke_Header_Flags.uniqueItemsBit)
    tst     r0, r1
    beq     SlotLoop_MakeTempPoolLoop_Add
    
    #printf("            enforcing unique items...")
    ; int itemId = *((short*)(currentPoolPokePtr + argDataOffsets[1]));
    ; if (ARM9::ContainsHalfword(&usedItems, i, itemId))
    ;     continue;
    add     r0, sp, #S_ArgPokeDataOffsets
    ldrb    r0, [r0, #1]
    ldrh    r2, [r6, r0]
    add     r0, sp, #S_UsedItems
    mov     r1, r4
    bl      ARM9::ContainsHalfword
    cmp     r0, #FALSE
    bne     SlotLoop_MakeTempPoolLoop_End
    
SlotLoop_MakeTempPoolLoop_Add:
    #printf("                Adding %d", r7)
    
    ; tempPool[tempPoolCount] = j;
    add     r0, sp, #S_TempPool
    ldrb    r1, [r0, #(S_TempPoolCount - S_TempPool)]
    #printf("                tempPoolCount=%d", r1)
    strb    r7, [r0, r1]
    
    ; tempPoolCount += 1;
    add     r1, #1
    strb    r1, [r0, #(S_TempPoolCount - S_TempPool)]

SlotLoop_MakeTempPoolLoop_End:
    ; j++
    add     r0, sp, #S_TempPool
    add     r7, #1
    
    ; j < trainerPokePtr->header.poolSizes[currentPoolId];
#if DEBUG
    add     r1, sp, #S_CurrentPoolId
    ldrh r1, [r1]
    #printf("                currentPoolId=%d", r1)
#endif

    ldr     r0, [sp, #ARG_TrainerPokePtr]
    add     r0, #TrainerPoke_Header.poolSizes[0]
    add     r1, sp, #S_CurrentPoolId
    ldrh    r1, [r1]
    ldrb    r0, [r0, r1]
    #printf("                Test %d", r0)
    cmp     r7, r0
    bcc     SlotLoop_MakeTempPoolLoop_Start


    ; int poolRand = (((long) ARM9::GFL_RandomMT()) * tempPoolCount) >> 32;
    bl      ARM9::GFL_RandomMT
    mov     r1, #0
    add     r2, sp, #S_TempPool
    ldrb    r2, [r2, #(S_TempPoolCount - S_TempPool)]
    mov     r3, #0
    blx     ARM9::Multiply64 ; use r1 as the result
    
    ; selectedPokeIndices[currentSlotId] = tempPool[poolRand];
    add     r0, sp, #S_TempPool
    ldrb    r0, [r0, r1]
    add     r1, sp, #S_SelectedPokeIndices
    ldr     r2, [sp, #S_CurrentSlotId]
    strb    r0, [r1, r2]
    
    #printf("            rand=%d", r0)

    ; TrainerPoke* selectedPoolPokePtr = trainerPokePtr + currentPoolOffset + pokeDataSize * tempPool[poolRand];
    ldr     r1, [sp, #ARG_PokeDataSize]
    mul     r0, r1
    add     r1, sp, #S_CurrentPoolId
    ldrh    r1, [r1, #(S_CurrentPoolOffset - S_CurrentPoolId)]
    add     r0, r1
    ldr     r1, [sp, #ARG_TrainerPokePtr]
    add     r6, r0, r1 ; r6 := selectedPoolPokePtr
    
    ; usedSpecies[i] = selectedPoolPokePtr->species;
    add     r0, sp, #S_UsedSpecies
    lsl     r1, r4, #1
    ldrh    r2, [r6, #TrainerPoke.species]
    strh    r2, [r0, r1]
    
    ; if (trainerDataPtr->flags & (1 << TrainerData_Flags.hasItemBit))
    ;     usedItems[i] = *((short*)(selectedPoolPokePtr + argDataOffsets[1]));
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    ldrh    r0, [r0, #TrainerData.flags]
    mov     r1, #(1 << TrainerData_Flags.hasItemBit)
    tst     r0, r1
    beq     SlotLoop_End
    
    add     r0, sp, #S_UsedItems
    lsl     r1, r4, #1
    add     r2, sp, #S_ArgPokeDataOffsets
    ldrb    r2, [r2, #1]
    ldrh    r2, [r6, r2]
    strh    r2, [r0, r1]
    
SlotLoop_End:
    add     r4, #1
    add     r0, sp, #S_SlotsRemaining
    ldrb    r0, [r0, #(S_SlotsRemainingCount - S_SlotsRemaining)]
    cmp     r0, #0
    ble     AddToTeamLoop_Setup
    b       SlotLoop_Start


AddToTeamLoop_Setup:
    #printf("    total=%d", r4)
    #printf("    add to team loop")
    mov     r7, r4
    mov     r4, #0
    
AddToTeamLoop_Start:
    #printf("        addIterationSlot=%d", r4)
    
	; TrainerPoke_Header_Slot currentSlot = trainerPokePtr->header.slots[i];
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    lsl     r1, r4, #1
    ldrh    r6, [r0, r1]
    
    ; TrainerPoke* pokeDataPtr = trainerPokePtr + poolOffsets[currentSlot.poolId] + pokeDataSize * selectedPokeIndices[i];
    add     r0, sp, #S_SelectedPokeIndices ; r0 := &selectedPokeIndices
    #printf("            &selectedPokeIndices=0x%08X", r0)
    ldrb    r0, [r0, r4] ; r0 := selectedPokeIndices[i]
    #printf("            selectedPokeIndices[%d]=0x%08X", r4, r0)
    ldr     r1, [sp, #ARG_PokeDataSize] ; r1 := pokeDataSize
    #printf("            pokeDataSize=%d", r1)
    mul     r0, r1 ; r0 := pokeDataSize * selectedPokeIndices[i]
    add     r1, sp, #S_PoolOffsets ; r1 := &poolOffsets
    #read_bits(r2, r6, TrainerPoke_Header_Slot.poolBit, TrainerPoke_Header_Slot.poolSize) ; r2 := currentSlot.poolId
    #printf("            currentSlot.poolId=%d", r2)
    lsl     r2, #1
    ldrh    r1, [r1, r2] ; r1 := poolOffsets[currentSlot.poolId]
    #printf("            poolOffsets[%d]=%d", r2, r1)
    add     r0, r1 ; r0 := poolOffsets[currentSlot.poolId] + pokeDataSize * selectedPokeIndices[i]
    ldr     r1, [sp, #ARG_TrainerPokePtr] ; r1 := &trainerPokePtr
    #printf("            trainerPokePtr=0x%0X", r1)
    add     r0, r1
    
    #printf("            species=%d", ldrh [r0, #TrainerPoke.species])
    
    ; pokeDataPtr->level = currentSlot.level;
    #read_bits(r1, r6, TrainerPoke_Header_Slot.levelBit, TrainerPoke_Header_Slot.levelSize)
    strb    r1, [r0, #TrainerPoke.level]
    
    ; ARM9::TrTool_MakePokeFromData(pokeDataPtr, pokeDataSize, playerId, trainerId, heapId, dataOffsets, teamRand, trainerDataPtr, tempPokePtr);
    ldr     r1, [sp, #ARG_PokeDataSize]
    ldr     r2, [sp, #S_PlayerId] ; playerId
    ldr     r3, [sp, #S_TrainerId] ; trainerId
    bl      ARM9::TrTool_MakePokeFromData
    
    ; int iv = currentSlot.iv;
    ; iv |= (iv << 5) | (iv << 10) | (iv << 15) | (iv << 20) | (iv << 25);
    ; ARM9::Poke_SetParam(argTempPokePtr, 172, iv);
    #read_bits(r0, r6, TrainerPoke_Header_Slot.ivBit, TrainerPoke_Header_Slot.ivSize)
    lsl     r2, r0, #5
    orr     r2, r0
    lsl     r2, #5
    orr     r2, r0
    lsl     r2, #5
    orr     r2, r0
    lsl     r2, #5
    orr     r2, r0
    lsl     r2, #5
    orr     r2, r0
    #printf("            combinedIvs=0x%08X", r2)
    ldr     r0, [sp, #ARG_TempPokePtr]
    mov     r1, #172 ; combined IVs
    bl      ARM9::Poke_SetParam

AddToTeamLoop_CheckHasNature:
    
    
AddToTeamLoop_Add:
    ; ARM9::PokeParty_Add(partyPtr, tempPokePtr);
    ldr     r0, [sp, #S_PartyPtr]
    ldr     r1, [sp, #ARG_TempPokePtr]
    bl      ARM9::PokeParty_Add
    #printf("            added poke to party 0x%08X", ldr [sp, #S_PartyPtr])
    

AddToTeamLoop_End:
    add     r4, #1
    cmp     r4, r7
    bcs     Return
    b       AddToTeamLoop_Start
    
    
Return:
    #printf("        pooled done!")
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}