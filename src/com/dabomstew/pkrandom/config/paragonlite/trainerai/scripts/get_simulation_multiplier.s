#DEFINE SIMULATION_USE_EFFECTIVENESS 0x00
#DEFINE SIMULATION_DEBUG_MODE 0x04
#DEFINE CURRENT_HP 0x08
#DEFINE MAX_HP 0x0C
#DEFINE ATTACKER_ID 0x10

#DEFINE VALUE_CURRENT_HP 0x0D
#DEFINE VALUE_MAX_HP 0x0E

#DEFINE TRAINER_AI_ENV_MOVE_ID 0x02
#DEFINE TRAINER_AI_ENV_PARAM 0x38
#DEFINE TRAINER_AI_ENV_SERVER_FLOW 0xB4
#DEFINE TRAINER_AI_ENV_ATTACKER 0xBC
#DEFINE TRAINER_AI_ENV_DEFENDER 0xC0
#DEFINE TRAINER_AI_ENV_RESULT 0xC4

    push    {r3-r7, lr}
    sub     sp, #0x14
    mov     r5, r0 ; r5 := *scriptVM
    mov     r4, r1 ; r4 := *trainerAIEnv
    mov     r7, r2
    bl      ARM9::Script_ReadArg
    mov     r6, r0
    
    mov     r0, r4 ; r4 := *trainerAIEnv
    add     r0, #TRAINER_AI_ENV_DEFENDER
    ldr     r0, [r0]
    mov     r1, #VALUE_CURRENT_HP
    bl      Battle::GetPokeStat
    str     r0, [sp, #CURRENT_HP]
    
    mov     r0, r4 ; r4 := *trainerAIEnv
    add     r0, #TRAINER_AI_ENV_DEFENDER
    ldr     r0, [r0]
    mov     r1, #VALUE_MAX_HP
    bl      Battle::GetPokeStat
    str     r0, [sp, #MAX_HP]
    
    mov     r0, r4 ; r4 := *trainerAIEnv
    add     r0, #TRAINER_AI_ENV_ATTACKER
    ldr     r0, [r0]
    bl      Battle::GetPokeId
    str     r0, [sp, #ATTACKER_ID]
    
    mov     r0, r4 ; r4 := *trainerAIEnv
    add     r0, #TRAINER_AI_ENV_DEFENDER
    ldr     r0, [r0]
    bl      Battle::GetPokeId
    mov     r2, r0
    
    mov     r0, #1
    str     r0, [sp, #SIMULATION_USE_EFFECTIVENESS]
    
    mov     r0, #0
    str     r0, [sp, #SIMULATION_DEBUG_MODE]
    
    mov     r0, r4 ; r4 := *trainerAIEnv
    add     r0, #TRAINER_AI_ENV_SERVER_FLOW
    ldrh    r3, [r4, #TRAINER_AI_ENV_MOVE_ID]
    ldr     r0, [r0]
    ldr     r1, [sp, #ATTACKER_ID]
    bl      Battle::Handler_SimulationDamage
    mov     r3, r0
;    str     r0, [r4, #TRAINER_AI_ENV_PARAM]
    
    ldr     r0, [sp, #CURRENT_HP]
    sub     r0, r3
    
    cmp     r0, #0
    bge     GetTotalPercentDealt
    mov     r0, #0
    
GetTotalPercentDealt:
    ldr     r1, [sp, #MAX_HP]
    sub     r0, r1, r0
    lsl     r0, #12 ; r0 *= 4096
    bl      ARM9::DivideModUnsigned
    str     r0, [r4, #TRAINER_AI_ENV_PARAM]    
    
    add     r4, #TRAINER_AI_ENV_RESULT
    ldr     r0, [r4]
    
    add     sp, #0x14
    pop     {r3-r7, pc}