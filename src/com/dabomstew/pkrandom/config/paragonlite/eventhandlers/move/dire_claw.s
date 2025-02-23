#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_CONDITION_ID 0x1D
#DEFINE VAR_CONDITION_ADDRESS 0x1E
#DEFINE VAR_EFFECT_CHANCE 0x26

#DEFINE CONDITION_POISON 5
#DEFINE CONDITION_PARALYZE 1
#DEFINE CONDITION_SLEEP 2

    push    {r3-r5, lr}
    sub     sp, #0x08
    mov     r5, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, #3
    bl      Battle::Random
    
CheckPoison:
    mov     r5, #CONDITION_POISON
    cmp     r0, #0
    beq     ApplyStatus

CheckParalysis:
    mov     r5, #CONDITION_PARALYZE
    cmp     r0, #1
    beq     ApplyStatus
    
ApplySleep:
    mov     r5, #CONDITION_SLEEP
    
ApplyStatus:
    mov     r0, r5
    bl      Battle::MakeNonVolatileStatus
    mov     r4, r0
    
    mov     r0, #VAR_CONDITION_ID
    mov     r1, r5
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #VAR_CONDITION_ADDRESS
    mov     r1, r4
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #VAR_EFFECT_CHANCE
    mov     r1, #(PARAGONLITE ? 30 : 50)
    bl      Battle::EventVar_RewriteValue
    
Return:
    add     sp, #0x08
    pop     {r3-r5, pc}