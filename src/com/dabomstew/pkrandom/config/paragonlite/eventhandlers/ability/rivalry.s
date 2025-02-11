    PUSH    {R4-R6, LR}
    MOV     R0, #3
    MOV     R5, R1
    MOV     R4, R2
    BL      Battle::EventVar_GetValue
    CMP     R4, R0
    BNE     End
    MOV     R0, R5
    MOV     R1, R4
    BL      Battle::GetPoke
    MOV     R4, R0
    MOV     R0, #4
    BL      Battle::EventVar_GetValue
    MOV     R1, R0
    LSL     R1, R1, #24
    MOV     R0, R5
    LSR     R1, R1, #24
    BL      Battle::GetPoke
    MOV     R5, R0
    
; Get Gender of Pokémon 1
    MOV     R0, R4
    MOV     R1, #18 ; gender
    BL      Battle::Poke_GetParam
    LSL     R0, R0, #24
    LSR     R4, R0, #24
    
; Get Gender of Pokémon 2
    MOV     R0, R5
    MOV     R1, #18 ; gender
    BL      Battle::Poke_GetParam
    LSL     R0, R0, #24
    LSR     R0, R0, #24
    
; Check if either is gender unknown (value of 2)
    CMP     R4, #2
    BEQ     End
    CMP     R0, #2
    BEQ     End
    
; Check for opposite gender
; This update makes it so we don't get the drawback from opposite-gender targets
    CMP     R4, R0
    BNE     End
    
; Same Gender
    MOV     R0, #49 ; move power
    ldr     R1, =4915 ; 1.2x
    BL      Battle::EventVar_MulValue
    
End:
    POP     {R4-R6, PC}
