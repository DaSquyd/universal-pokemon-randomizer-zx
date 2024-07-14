    push    {r4, lr}
    mov     r4, r1
    bl      ARM9::Script_ReadArg
    
    str     r0, [r4, #0x38]
    add     r4, #0xC4
    ldr     r0, [r4]
    bl      

;    push    {r3-r7, lr}
;    add     sp, #-16
;    mov     r5, r0
;    mov     r4, r1
;    mov     r7, r2
;    bl      ARM9::Script_ReadArg
;    
;;; store current HP to [sp, #8]
;;    mov     r0, r4
;;    add     r0, #0xC0
;;    ldr     r0, [r0]
;;    mov     r1, #13 ; current HP index
;;    bl      Battle::GetPokeStat
;;    str     r0, [sp, #8]
;;    
;;; store PokeId_0 to [sp, #12]
;;    mov     r0, r4
;;    add     r0, #0xBC
;;    ldr     r0, [r0]
;;    bl      Battle::GetPokeId
;;    str     r0, [sp, #12]
;;    
;;; store PokeId_1 to r2
;;    mov     r0, r4
;;    add     r0, #0xC0
;;    ldr     r0, [r0]
;;    bl      Battle::GetPokeId
;;    mov     r2, r0              ; r2 := pokeId_1
;;    
;;    mov     r0, #1
;;    str     r0, [sp, #0]
;;    
;;    mov     r0, #0
;;    str     r0, [sp, #4]
;;    
;;    mov     r0, r4
;;    add     r0, #0xB4
;;    ldr     r0, [r0]            ; r0 := context???
;;    ldr     r1, [sp, #12]       ; r1 := pokeId_0
;;    ldrh    r3, [r4, #2]        ; r3 := move
;;    bl      Battle::AI_CalcDamage
;;    str     r0, [r4, #0x38]
;    
;    add     r4, #0xC4
;    ldr     r0, [r4]
;    pop     {r3-r7, pc}