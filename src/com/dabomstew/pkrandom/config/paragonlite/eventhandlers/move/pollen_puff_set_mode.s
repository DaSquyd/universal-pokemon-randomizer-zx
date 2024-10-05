; Determines if we're targeting a foe or ally, stores that value, and updates the animation accordingly

#DEFINE DAMAGE_ANIM_INDEX 1
#DEFINE HEAL_ANIM_INDEX 3

    push    {r4-r6, lr}
    mov     r6, r1
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, #0x04
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    beq     CheckMode
    
Heal:
    mov     r0, #0x51 ; flag as heal
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    str     r0, [r4]
    
CheckMode:
    mov     r1, #HEAL_ANIM_INDEX
    ldr     r0, [r4]
    cmp     r0, #0
    bne     SetMoveAnimationIndex
    mov     r1, #DAMAGE_ANIM_INDEX
    
SetMoveAnimationIndex:
    mov     r0, r6
    bl      Battle::Handler_SetMoveAnimationIndex
    
Return:
    pop     {r4-r6, pc}