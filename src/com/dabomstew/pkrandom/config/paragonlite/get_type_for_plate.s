; Check Pixie Plate
    mov     r1, #(644 >> 2) ; Pixie Plate
    lsl     r1, #2
    cmp     r0, r1
    bne     Main
    
Fairy:
    mov     r0, #17
    bx      lr
    
; Blank plate always returns 0 (Normal)
Main:
    mov     r1, #(298 >> 1) ; Flame Plate
    lsl     r1, #1
    sub     r0, r1
    cmp     r0, #15
    bhi     Other
    
    add     r0, pc
    ldrb    r0, [r0, #2]
    bx      lr
    
    dcb     9  ; Flame Plate
    dcb     10 ; Splash Plate
    dcb     12 ; Zap Plate
    dcb     11 ; Meadow Plate
    dcb     14 ; Icicle Plate
    dcb     1  ; Fist Plate
    dcb     3  ; Toxic Plate
    dcb     4  ; Earth Plate
    dcb     2  ; Sky Plate
    dcb     13 ; Mind Plate
    dcb     6  ; Insect Plate
    dcb     5  ; Stone Plate
    dcb     7  ; Spooky Plate
    dcb     15 ; Draco Plate
    dcb     16 ; Dread Plate
    dcb     8  ; Iron Plate
    
Other:
    mov     r0, #0
    bx      lr
