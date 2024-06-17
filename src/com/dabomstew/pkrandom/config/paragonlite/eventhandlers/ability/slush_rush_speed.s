    PUSH {R4-R5, LR}
    MOV  R0, #2
    MOV  R5, R1
    MOV  R4, R2
    BL   Battle::EventVar_GetValue
    CMP  R4, R0
    BNE  End
    MOV  R0, R5
    BL   Battle::GetWeather
    CMP  R0, #3
    BNE  End
    MOV  R0, #53 ; stat
    ldr  R1, =8192 ; 2x
    BL   Battle::EventVar_MulValue
    
End:
    POP  {R4-R5, PC}