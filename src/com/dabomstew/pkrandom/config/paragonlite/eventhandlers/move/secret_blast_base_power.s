#DEFINE IN_MIN 10
#DEFINE IN_MAX 40
#DEFINE OUT_MIN 40
#DEFINE OUT_MAX 90

#DEFINE GCD (function gcd(numA, numB) { return numA == 0 ? numB : gcd(numB % numA, numA) } gcd(IN_MIN, gcd(IN_MAX, gcd(OUT_MIN, OUT_MAX))))

#DEFINE MULTIPLIER ((OUT_MAX - OUT_MIN) / GCD)
#DEFINE DIVISOR (Math.round(4096 / ((IN_MAX - IN_MIN) / GCD)))

    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    mov     r1, #BPV_Level
    bl      Battle::Poke_GetParam
    
    mov     r1, #OUT_MAX
    cmp     r0, #IN_MAX
    bcs     ApplyMod
    
    mov     r1, #OUT_MIN
    cmp     r0, #IN_MIN
    bls     ApplyMod
    
    sub     r0, #IN_MIN
    mov     r1, #MULTIPLIER
    mul     r0, r1
    mov     r1, #DIVISOR
    bl      Battle::FixedRound
    add     r1, r0, #OUT_MIN
    
ApplyMod:
    mov     r0, #VAR_MoveBasePower
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}