; Max size: 72

    push    {r3-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    
; Get Target HP
    mov     r1, #2 ; 1/2 HP
    bl      Battle::DivideMaxHP
    mov     r5, r0
    
; Get current health
    mov     r0, r6
    mov     r1, #0x0D
    bl      Battle::GetPokeStat
    
; Branch if current hp > target
    cmp     r0, r5
    bhi     Return
    
; Check move type (passed in as r2 initially)
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x35 ; offensive stat
    ldr     r1, =5325 ; 1.3x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r7, pc}