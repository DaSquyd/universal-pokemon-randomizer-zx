; Max size: 72

    push    {r3-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    
; Get Target HP
    mov     r1, #3 ; 1/3 HP
    bl      Battle::DivideMaxHP
    mov     r5, r0
    
; Get current health
    mov     r0, r6
    mov     r1, #0x0D
    bl      Battle::GetPokeStat
    
; Return if current hp > target
    cmp     r0, r5
    bhi     Return
    
; Check move type (passed in as r2 initially)
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x35 ; offensive stat
    mov     r1, #4915 ; 1.2x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r7, pc}