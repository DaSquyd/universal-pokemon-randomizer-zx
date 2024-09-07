; r0: *battleClientWk
; r1: attackingPokeId
; r2; defendingPokeId

    push    {r3-r7, lr}
    mov     r6, r0 ; r6 := *battleClientWk
    mov     r5, r1 ; r5 := attackingPokeId
    mov     r4, r2 ; r4 := defendingPokeId
    
    ; get *MainModule
    ldr     r7, [r6, #BTL_CLIENT_WK_MAIN_MODULE]
    
    ; get clientId
    mov     r0, #BTL_CLIENT_WK_ID
    ldrb    r3, [r6, r0]
    
    ; ensure that the defendingPokeId belongs to this client
    mov     r0, r4 ; r0 := defendingPokeId
    bl      Battle::PokeIdToClientId
    cmp     r0, r3
    bne     ReturnZero ; defendingPokeId is not on the client's team
    
    ; ensure that the attackingPokeId belongs to an enemy team
    mov     r0, r5 ; r0 := attackingPokeId
    bl      Battle::PokeIdToClientId
    mov     r2, r0 ; r2 := attackingPokeClientId
    mov     r1, r3 ; r1 := thisClientId
    mov     r0, r7 ; r0 := *mainModule
    bl      Battle::MainModule_AreClientIDsOpposing
    cmp     r0, #0
    beq     ReturnZero ; same team    
    
    ; We shift down to one of the following to keep it within 0-11
    mov     r0, r5
    bl      Battle::BattleClient_GetAIAttackingPokeIdx
    mov     r5, r0
    
    ; clamp the defending poke between 0 and 5
    mov     r0, r4
    bl      Battle::BattleClient_GetAIDefendingPokeIdx
    mov     r4, r0
    
GetThreatValue:
    mov     r0, #BTL_CLIENT_WK_AI_MEM
    add     r0, r6 ; r0 := &aiMem
    
    mov     r1, #AI_MEM_POKE_SIZE
    mul     r1, r5 ; r1 := pokeSize * attackingPokeIdx
    add     r0, r1 ; r0 := &aiMem + (pokeSize * attackingPokeIdx)
    
    mov     r1, #AI_MEM_THREAT_ARR
    add     r0, r1 ; r0 := &aiMem + (pokeSize * attackingPokeIdx) + threatArr
    
    mov     r1, #AI_MEM_THREAT_VALUE_SIZE
    mul     r1, r4 ; r1 := threatValueSize * defendingPokeIdx  
    ldrh    r0, [r0, r1] ; r0 := *(&aiMem + (pokeSize * attackingPokeIdx) + threatArr + (threatValueSize * defendingPokeIdx))
    
    pop     {r3-r7, pc}
    
ReturnZero:
    mov     r0, #0
    pop     {r3-r7, pc}