; Max size: 72

    push    {r3-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    
; Get Target HP
#if PARAGONLITE
    mov     r1, #2 ; 1/2 HP
#else
    mov     r1, #3 ; 1/3 HP
#endif
    bl      Battle::DivideMaxHP
    mov     r5, r0
    
; Get current health
    mov     r0, r6
    mov     r1, #0x0D
    bl      Battle::GetPokeStat
    
; Branch if current hp > target
    cmp     r0, r5
    bhi     Return
    
; Check move type
    mov     r0, #VAR_MoveType ; move type
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_Ratio
#if PARAGONLITE
    ldr     r1, =5325 ; 1.3x
#elif REDUX
    ldr     r1, =4915 ; 1.2x
#else
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
#endif
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r7, pc}