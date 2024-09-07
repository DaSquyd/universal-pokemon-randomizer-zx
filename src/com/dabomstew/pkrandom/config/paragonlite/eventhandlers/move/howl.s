#DEFINE NUM_POKES 0x00
#DEFINE POKES_ARRAY 0x04

#DEFINE ADD_STACK_SIZE 0x08

#DEFINE EFFECT_CHANGE_STAT_STAGE 0x0E
#DEFINE STATSTAGE_ATTACK 0x01

    push    {r3-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    mov     r5, r1
    mov     r4, r2
    
;    mov     r0, r5
;    mov     r1, r4
;    bl      Battle::Handler_PokeIDToPokePos
;    mov     r2, r0
;    
;    mov     r0, r5
;    mov     r1, #2
;    lsl     r1, #9 ; 00000100_00000000
;    orr     r1, r2
;    add     r2, sp, #POKES_ARRAY
;    bl      Battle::Handler_ExpandPokeID
;    str     r0, [sp, #NUM_POKES]
;    cmp     r0, #0
;    bls     Return
    
    mov     r0, r5 ; server_flow
    mov     r1, #EFFECT_CHANGE_STAT_STAGE
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r1, #STATSTAGE_ATTACK
    str     r1, [r7, #0x04] ; rank type
    strb    r1, [r7, #0x0C] ; rank volume
    strb    r1, [r7, #0x0E]
;    ldr     r0, [sp, #NUM_POKES]
;    strb    r0, [r7, #0x0F]
    
;    mov     r3, #0 ; iteration
 
    
    mov     r0, #1
    strb    r0, [r7, #0x0F]
    mov     r0, r4
    strb    r0, [r7, #0x10]
    
;LoopStart:
;    add     r0, sp, #POKES_ARRAY
;    ldrb    r1, [r0, r3]   
;    add     r0, r7, r3
;    strb    r1, [r0, #0x10]
;    
;    add     r3, #1
;    ldr     r0, [sp, #NUM_POKES]
;    cmp     r3, r0
;    bcc     LoopStart
    
PopWork:
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #ADD_STACK_SIZE
    pop     {r3-r7, pc}