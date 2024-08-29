#DEFINE EFFECTIVENESS_ZERO 0
#DEFINE EFFECTIVENESS_NEUTRAL 3
#DEFINE EFFECTIVENESS_DOUBLE 4

#DEFINE STACK_SIZE 0x18

#DEFINE POKE (STACK_SIZE - 0x18)
#DEFINE ARG_0 (STACK_SIZE - 0x00)

    push    {r3-r7, lr}
    ldr     r7, =0x3021    
    mov     r6, r3
    mov     r5, r0
    mov     r4, r2
    
    mov     r0, r7
    str     r1, [sp, #POKE]    
    bl      Battle::EventVar_Push
    ldr     r0, [sp, #POKE]
    
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #0x03
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #0x04
    bl      Battle::EventVar_SetConstValue
    
    add     r1, sp, #ARG_0
    ldrb    r1, [r1]
    mov     r0, #0x15 ; target type
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x16 ; move type
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x4B ; Use neutral instead of immune
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, #0x4C ; Override effectiveness
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r5
    mov     r1, r4
    bl      BattleServer::ConditionEffectivenessModifiers ; Miracle Eye, Foresight, Odor Sleuth, Ingrain, and Grounded
    
    mov     r0, r5
    mov     r1, #0x3E
    bl      Battle::Event_CallHandlers
    
    mov     r0, #0x4B
    bl      Battle::EventVar_GetValue
    mov     r5, r0
    
    mov     r0, #0x4C
    bl      Battle::EventVar_GetValue
    mov     r4, r0
    
    add     r7, #0x0D
    mov     r0, r7
    bl      Battle::EventVar_Pop
    
CheckOverride:
    cmp     r4, #0
    beq     CheckIgnoreImmune
    
HandleOverride:
    cmp     r5, #0
    beq     ReturnNeutral
    
    mov     r0, r5
    pop     {r3-r7, pc}
    
    
CheckIgnoreImmune:
    add     r1, sp, #ARG_0
    ldrb    r1, [r1]
    mov     r0, r6
    bl      Battle::GetEffectivenessForAttack
    cmp     r0, #EFFECTIVENESS_ZERO
    bne     Return
    
    cmp     r5, #0
    beq     Return ; not ignoring immunity
    
ReturnNeutral:
    mov     r0, #EFFECTIVENESS_NEUTRAL
    
    
Return:
    pop     {r3-r7, pc}