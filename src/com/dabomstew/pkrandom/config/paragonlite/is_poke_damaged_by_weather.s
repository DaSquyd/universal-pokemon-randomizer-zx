    push    {r4, lr}
    mov     r4, r0
    
    cmp     r1, #3 ; hail
    beq     HailFighting
    
    cmp     r1, #4 ; sandstorm
    bne     ReturnFalse
    
SandstormGround:
    mov     r1, #4 ; Ground-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     SandstormRock
    b       ReturnFalse
    
SandstormRock:
    mov     r0, r4
    mov     r1, #5 ; Rock-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     SandstormSteel
    b       ReturnFalse
    
SandstormSteel:
    mov     r0, r4
    mov     r1, #8 ; Steel-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     ReturnTrue
    b       ReturnFalse
    
HailFighting:
    mov     r1, #1 ; Fighting-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     HailIce
    b       ReturnFalse
    
HailIce:
    mov     r0, r4
    mov     r1, #14 ; Ice-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     ReturnTrue
    
ReturnFalse:
    mov     r0, #0
    pop     {r4, pc}
    
ReturnTrue:
    mov     r0, r4
    mov     r1, #0xE ; Total HP
    bl      Battle::GetPokeStat
    asr     r1, r0, #3
    lsr     r1, #28
    add     r1, r0
    lsl     r0, r1, #12
    lsr     r0, #16
    bne     Ignore
    mov     r0, #1
Ignore:
    pop     {r4, pc}
    
    
