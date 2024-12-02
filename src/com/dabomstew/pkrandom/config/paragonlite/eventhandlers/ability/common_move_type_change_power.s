#define STRUGGLE 165
#define HIDDEN_POWER 237
#define WEATHER_BALL 311
#define NATURAL_GIFT 363
#define JUDGMENT 449
#define TECHNO_BLAST 546
#define REVELATION_DANCE 681

    push    {r4-r5, lr}
    mov     r4, r0
    mov     r5, r1
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; Ensure that we've successfully changed to the right move type
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveType ; base (unmodified) move type
    cmp     r0, #TYPE_Normal
    bne     Return
    
    cmp     r0, #STRUGGLE
    beq     Return
    
    mov     r1, #HIDDEN_POWER
    cmp     r0, r1
    beq     Return
    
    add     r1, #(WEATHER_BALL - HIDDEN_POWER)
    cmp     r0, r1
    beq     Return
    
    add     r1, #(NATURAL_GIFT - WEATHER_BALL)
    cmp     r0, r1
    beq     Return
    
    add     r1, #(JUDGMENT - NATURAL_GIFT)
    cmp     r0, r1
    beq     Return
    
    add     r1, #(TECHNO_BLAST - JUDGMENT)
    cmp     r0, r1
    beq     Return
    
    add     r1, #(REVELATION_DANCE - TECHNO_BLAST)
    cmp     r0, r1
    beq     Return
    
    mov     r0, #VAR_MovePower
    ldr     r1, =(0x1000 * 1.2)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r5, pc}