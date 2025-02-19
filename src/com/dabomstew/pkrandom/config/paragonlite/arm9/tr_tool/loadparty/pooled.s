; r0: playerId
; r1: trainerId
; r2: partyPtr
; r3: heapId
; arg0: trainerDataPtr
; arg4: trainerPokePtr
; arg8: pokePtr

; Handles randomization from trainer poke pools

; Trainer_MakePoke args
#define S_HeapId 0x00
#define S_DataOffsets 0x04 ; [0] = statModifiers, [1] = item, [2] = moves
#define S_TeamRand 0x08
#define S_TrainerDataPtr 0x0C
#define S_ArgTempPokePtr 0x10

#define S_PlayerId 0x14
#define S_TrainerId 0x18
#define S_PartyPtr 0x1C
#define S_PokeSize 0x24
#define S_PokeIndex 0x2C
#define STACK_SIZE 0x34

#define PUSH_SIZE (5 * 4) ; r4-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_TrainerDataPtr (ARG_OFFSET + 0x00)
#define ARG_TrainerPokePtr (ARG_OFFSET + 0x04)
#define ARG_TempPokePtr (ARG_OFFSET + 0x08)

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    str     r0, [sp, #S_PlayerId]
    str     r1, [sp, #S_TrainerId]
    str     r2, [sp, #S_PartyPtr]
    str     r3, [sp, #S_HeapId]
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    str     r0, [sp, #S_TrainerDataPtr]
    ldr     r0, [sp, #ARG_TempPokePtr]
    str     r0, [sp, #S_ArgTempPokePtr]
    
    #printf("Standard trainer party generation...")
    
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}