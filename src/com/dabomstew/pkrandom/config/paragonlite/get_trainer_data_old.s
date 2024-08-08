#DEFINE FILE_SIZE 0x14

    push    {r3-r6, lr}             ; 0x0203050C    0x02A015E4
    add     sp, #-FILE_SIZE         ; 0x0203050E    0x02A015E6
    mov     r5, r1                  ; 0x02030510    0x02A015E8
    mov     r1, sp                  ; 0x02030512    0x02A015EA
    mov     r6, sp                  ; 0x02030514    0x02A015EC
    bl      ARM9::LoadTrainerFile   ; 0x02030516    0x02A015EE
    cmp     r5, #11                 ; 0x0203051A    0x02A015F2
    bhi     Return                  ; 0x0203051C    0x02A015F4
    
    #SWITCH r5                      ; 0x0203051E    0x02A015F6
    #CASE Flags                     ; 0x0203052A    0x02A01602
    #CASE Class                     ; 0x0203052C    0x02A01604
    #CASE BattleType                ; 0x0203052E    0x02A01606
    #CASE PartySize                 ; 0x02030530    0x02A01608
    #CASE Item                      ; 0x02030532    0x02A0160A
    #CASE Item                      ; 0x02030534    0x02A0160C
    #CASE Item                      ; 0x02030536    0x02A0160E
    #CASE Item                      ; 0x02030538    0x02A01610
    #CASE AIFlags                   ; 0x0203053A    0x02A01612
    #CASE IsHealer                  ; 0x0203053C    0x02A01614
    #CASE RewardMoneyScale          ; 0x0203053E    0x02A01616
    #CASE RewardItem                ; 0x02030540    0x02A01618
    
Flags:
    ldrb    r4, [r6, #0x00]         ; 0x02030542    0x02A0161A
    b       Return                  ; 0x02030544    0x02A0161C
    
Class:
    ldrb    r4, [r6, #0x01]         ; 0x02030546    0x02A0161E
    b       Return                  ; 0x02030548    0x02A01620
    
BattleType:
    ldrb    r4, [r6, #0x02]         ; 0x0203054A    0x02A01622
    b       Return                  ; 0x0203054C    0x02A01624
    
PartySize:
    ldrb    r4, [r6, #0x03]         ; 0x0203054E    0x02A01626
    b       Return                  ; 0x02030550    0x02A01628
    
Item:
    sub     r0, r5, #4              ; 0x02030552    0x02A0162A
    lsl     r1, r0, #1              ; 0x02030554    0x02A0162C
    add     r0, sp, #4              ; 0x02030556    0x02A0162E
    ldrh    r4, [r0, r1]            ; 0x02030558    0x02A01630
    b       Return                  ; 0x0203055A    0x02A01632
    
AIFlags:
    ldr     r4, [sp, #0x0C]         ; 0x0203055C    0x02A01634
    b       Return                  ; 0x0203055E    0x02A01636
    
IsHealer:
    ldrb    r0, [r6, #0x10]         ; 0x02030560    0x02A01638
    lsl     r0, #31                 ; 0x02030562    0x02A0163A
    lsr     r4, r0, #31             ; 0x02030564    0x02A0163C
    b       Return                  ; 0x02030566    0x02A0163E
    
RewardMoneyScale:
    ldrb    r4, [r6, #0x11]         ; 0x02030568    0x02A01640
    b       Return                  ; 0x0203056A    0x02A01642
    
RewardItem:
    ldrh    r4, [r6, #0x12]         ; 0x0203056C    0x02A01644

Return:
    mov     r0, r4                  ; 0x0203056E    0x02A01646
    add     sp, #FILE_SIZE          ; 0x02030570    0x02A01648
    pop     {r3-r6, pc}             ; 0x02030572    0x02A0164A