    push    {r4, lr}
    mov     r4, r0
    
    cmp     r1, #WEATHER_Hail
    beq     HailFighting
    
    cmp     r1, #WEATHER_Sand
    bne     ReturnZero
    
SandstormGround:
    mov     r1, #TYPE_Ground
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     SandstormRock
    b       ReturnZero
    
SandstormRock:
    mov     r0, r4
    mov     r1, #TYPE_Rock
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     SandstormSteel
    b       ReturnZero
    
SandstormSteel:
    mov     r0, r4
    mov     r1, #TYPE_Steel
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     ReturnTrue
    b       ReturnZero
    
HailFighting:
    mov     r1, #TYPE_Fighting
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     HailIce
    b       ReturnZero
    
HailIce:
    mov     r0, r4
    mov     r1, #TYPE_Ice
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     ReturnTrue
    
ReturnZero:
    mov     r0, #0
    pop     {r4, pc}
    
ReturnTrue:
    mov     r0, r4
    mov     r1, #BPV_MaxHP
    bl      Battle::Poke_GetParam
    lsr     r0, #4
    bne     Return ; max between 1/16 HP and 1
    
    mov     r0, #1
    
Return:
    pop     {r4, pc}
    
    
