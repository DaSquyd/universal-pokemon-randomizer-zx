; r0: gameDataPtr (NEW)
; r1: trainerId
; r2: partyPtr
; r3: heapId

; GFL_HeapAllocate Args
#define S_ArgLineNum 0x00

; Trainer_LoadParty Args
#define S_ArgTrainerDataPtr 0x00
#define S_ArgTrainerPokePtr 0x04
#define S_ArgPokePtr 0x08
#define S_ArgPokeDataSize 0x0C
#define S_ArgPokeDataOffsets 0x10 ; [0] = statModifiers, [1] = item, [2] = moves

; stack
#define S_GameDataPtr = 0x14
#define S_PlayerId = 0x18
#define S_TrainerDataPtr = 0x1C
#define S_TrainerPokePtr = 0x20
#define S_TempPokePtr = 0x24

#define STACK_SIZE 0x28

    push    {r3-r7, lr}
    sub     sp, #STACK_SIZE
    str     r0, [sp, #S_GameDataPtr]
    mov     r5, r1
    mov     r6, r2
    mov     r7, r3
    
    #printf("ARM9::TrTool_LoadParty_Core (LR=0x%08X)", lr)
    #printf("Loading Trainer with id %d", r5)
    
    ; get playerId
    ldr     r0, [sp, #S_GameDataPtr]
    bl      ARM9::GameData_GetPlayerState
    bl      ARM9::PlayerState_GetId
    str     r0, [sp, #S_PlayerId]
    
    #printf("Player ID: 0x%08X", r5)
    
    ; r4 := heap low id 
    mov     r4, #1
    lsl     r4, #15 ; r0 := 0x8000
    sub     r1, r4, #1 ; r1 := 0x7FFF
    and     r1, r7 ; r1 &= heapId
    orr     r4, r1 ; r4 := 0x8000 | (0x7FFF & heapId)
    
    mov     r0, r6
    mov     r1, #6 ; max party size
    bl      ARM9::PokeParty_InitCore
    
    ; Allocate Trainer Data
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNum]
    mov     r0, r4 ; heapLowId
    mov     r1, #TrainerData.SIZE
    mov     r2, #FALSE ; clear
    ldr     r3, =ARM9::Data_aTrToolC ; "tr_tool.c"
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_TrainerDataPtr]
    
    #printf("TrainerData size: %d", #TrainerData.SIZE)
    
    ; Load in Trainer Data
    mov     r0, r5
    ldr     r1, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_LoadTrainerData
    
    ; Allocate Trainer Poke
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_GetPokeFileSize
    mov     r1, r0
    #printf("TrainerPoke size: %d", r0)
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNum]
    mov     r0, r4 ; heapLowId
    mov     r2, #FALSE ; clear
    ldr     r3, =ARM9::Data_aTrToolC ; "tr_tool.c"
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_TrainerPokePtr]
    
    ; Load in Trainer Poke
    mov     r0, r5
    ldr     r1, [sp, #S_TrainerPokePtr]
    bl      ARM9::TrTool_LoadTrainerPoke
    
    ; Allocate Poke
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNum]
    mov     r0, r4 ; heapLowId
    mov     r1, #PartyPoke.SIZE
    mov     r2, #FALSE ; clear
    ldr     r3, =ARM9::Data_aTrToolC ; "tr_tool.c"
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_TempPokePtr]
    
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_IsPooled
    mov     r4, r0
    
    ; setup    
    ldr     r0, [sp, #S_TrainerDataPtr] ; trainerDataPtr
    str     r0, [sp, #S_ArgTrainerDataPtr] ; trainerDataPtr
    add     r1, sp, #S_ArgPokeDataOffsets
    bl      ARM9::TrTool_GetPokeDataSize
    str     r0, [sp, #S_ArgPokeDataSize]
    
    #printf("offsets: 0x%06X", ldr [sp, #S_ArgPokeDataOffsets])
    
    ldr     r0, [sp, #S_TrainerPokePtr]
    str     r0, [sp, #S_ArgTrainerPokePtr] ; trainerPokePtr
    
    ldr     r0, [sp, #S_TempPokePtr]
    str     r0, [sp, #S_ArgPokePtr] ; pokePtr
    
    ldr     r0, [sp, #S_PlayerId]
    mov     r1, r5 ; trainerId
    mov     r2, r6 ; partyPtr
    mov     r3, r7 ; heapId
    
    cmp     r4, #FALSE
    bne     Pooled
    
Standard:
    bl      ARM9::TrTool_LoadParty_Standard ; see arm9/tr_tool/loadparty/standard.s
    b       Free
    
Pooled:
    bl      ARM9::TrTool_LoadParty_Pooled ; see arm9/tr_tool/loadparty/pooled.s

    
Free:
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::GFL_HeapFree
    ldr     r0, [sp, #S_TrainerPokePtr]
    bl      ARM9::GFL_HeapFree
    ldr     r0, [sp, #S_TempPokePtr]
    bl      ARM9::GFL_HeapFree
    
    add     sp, #STACK_SIZE
    pop     {r3-r7, pc}