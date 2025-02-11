; r0: trainerId
; r1: partyPtr
; r2: trainerDataPtr
; r3: trainerPokePtr
; arg0: pokePtr

; Handles randomization from trainer poke pools

#define S_PokeSize 0x00
#define STACK_SIZE 0x04

#define PUSH_SIZE (5 * 4) ; r3-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_PokePtr (ARG_OFFSET + 0x00)

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    mov     r4, r0 ; trainerId
    mov     r5, r1 ; partyPtr
    mov     r6, r2 ; trainerDataPtr
    mov     r7, r3 ; trainerPokePtr
    
    mov     r4, 
    
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}