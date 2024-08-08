; Check Pixie Plate
    cmp     r0, #115
    bne     Main
    
Fairy:
    mov     r0, #17
    bx      lr
    
; Blank plate always returns 0 (Normal)
Main:
    mov     r1, #149
    lsl     r1, #1 ; 298
    sub     r0, r1
    cmp     r0, #15
    bhi     Normal
    
    #SWITCH r0
    #CASE Fire
    #CASE Water
    #CASE Electric
    #CASE Grass
    #CASE Ice
    #CASE Fighting
    #CASE Poison
    #CASE Ground
    #CASE Flying
    #CASE Psychic
    #CASE Bug
    #CASE Rock
    #CASE Ghost
    #CASE Dragon
    #CASE Dark
    #CASE Steel
    
Fire:
    mov     r0, #9
    bx      lr
    
Water:
    mov     r0, #10
    bx      lr
    
Electric:
    mov     r0, #12
    bx      lr
    
Grass:
    mov     r0, #11
    bx      lr
    
Ice:
    mov     r0, #14
    bx      lr
    
Fighting:
    mov     r0, #1
    bx      lr
    
Poison:
    mov     r0, #3
    bx      lr
    
Ground:
    mov     r0, #4
    bx      lr
    
Flying:
    mov     r0, #2
    bx      lr
    
Psychic:
    mov     r0, #13
    bx      lr
    
Bug:
    mov     r0, #6
    bx      lr
    
Rock:
    mov     r0, #5
    bx      lr
    
Ghost:
    mov     r0, #7
    bx      lr
    
Dragon:
    mov     r0, #15
    bx      lr
    
Dark:
    mov     r0, #16
    bx      lr
    
Steel:
    mov     r0, #8
    bx      lr
    
Normal:
    mov     r0, #0
    bx      lr
