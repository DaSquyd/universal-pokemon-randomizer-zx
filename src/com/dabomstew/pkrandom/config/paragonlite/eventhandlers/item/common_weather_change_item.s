    push    {r3-r7, lr}
    mov     r5, r0
    mov     r0, #2
    mov     r4, r1
    mov     r6, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
;    ; Text
;    mov     r0, r5
;    mov     r1, #0x23
;    mov     r2, r6
;    bl      Battle::Handler_PushWork
;    mov     r7, r0
;    
;    add     r0, #8
;    mov     r1, #2 ; File 0x12
;    ldr     r2, =1191 ; "[poke]'s [item] began to glow!"
;    bl      Battle::Handler_StrSetup
;    
;    ; Pokémon
;    mov     r1, r6
;    mov     r0, r7
;    add     r0, #8
;    bl      Battle::Handler_AddArg
;    
;    ; Item
;    mov     r0, r5
;    bl      Battle::EventObject_GetSubId
;    mov     r1, r0
;    mov     r0, r7
;    add     r0, #8
;    bl      Battle::Handler_AddArg
;    
;    ; Pop Text
;    mov     r0, r4
;    mov     r1, r7
;    bl      Battle::Handler_PopWork
    
    
    ; Set weather
    mov     r0, r5
    mov     r1, #0x1D
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    strb    r6, [r1, #4]
    mov     r0, #3 ; turn count
    strb    r0, [r1, #5]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
End:
    pop     {r3-r7, pc}