; r0: serverFlow
; r1: pokeId

    push    {r3, lr}
    mov     r2, r0
    bl      Battle::GetPoke
    mov     r1, r0
    mov     r0, r2
    bl      Battle::GetEffectiveWeather
    pop     {r3, pc}