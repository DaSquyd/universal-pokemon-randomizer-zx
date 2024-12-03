; Max size: 72

    push    {r3-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
; Check move type
    mov     r0, #VAR_MoveType ; move type
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
; Get Target HP
    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    mov     r1, #(PARAGONLITE ? 2 : 3) ; 1/2 HP in ParagonLite, 1/3 HP otherwise
    bl      Battle::DivideMaxHP
    mov     r5, r0
    
; Get current health
    mov     r0, r6
    mov     r1, #0x0D
    bl      Battle::GetPokeStat
    
; Branch if current hp > target
    cmp     r0, r5
#if REDUX
    bls     ApplyFullRatio
    
    ; in Redux, there's at least a 1.2x bonus
    ldr     r1, =(0x1000 * 1.2)
    b       MulValue
#else
    bhi     Return
#endif
    
ApplyFullRatio:
#if PARAGONLITE
    ldr     r1, =(0x1000 * 1.3)
#else
    mov     r1, #(0x1800 >> 10) ; 1.5x
    lsl     r1, #10
#endif

MulValue:
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r7, pc}
    