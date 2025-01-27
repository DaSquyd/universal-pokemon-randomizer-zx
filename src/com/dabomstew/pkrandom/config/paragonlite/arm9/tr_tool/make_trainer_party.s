#define TRUE 1
#define FALSE 0

#define S_ArgLineNumber         0x00

#define S_ArgHeapId             0x00
#define S_PokeIVs               0x04
#define S_TrainerFlagsOffset    0x08
#define UNKNOWN                 0x0C
#define S_LcgState_Low          0x10
#define S_LcgState_High         0x14

#define S_TrainerNum            0x18
#define S_PartyPtr              0x1C
#define S_HeapId                0x20

#define S_TrainerData           0x24
#define S_TrainerPokeData       0x28
#define PARTY_POKES             0x2C

#define POOL_SIZE               0x30

#define POKE_SIZE               0x34
#define POKE_ITEM_OFFSET        0x38
#define POKE_MOVES_OFFSET       0x3C
#define POKE_EVS_OFFSET         0x40
#define POKE_RANDOM_MODE        0x44
    
#define POKE_INDEX              0x48
#define POKE_OFFSET             0x4C
#define POKE_ID                 0x50
#define POKE_SPECIES            0x54
#define POKE_LEVEL              0x58
#define POKE_DIFFICULTY         0x5C
#define POKE_FLAGS              0x60
#define POKE_RANDOM_ID          0x64

#define SELECTED_POKES          0x68 ; 8 bytes, only first 6 are used (one for each index)
#define SELECTED_COUNT          0x70

#define CHANNEL                 0x74

#define BUFFER                  0x78
#define BUFFER_MAX_SIZE         32
#define BUFFER_SIZE             BUFFER + BUFFER_MAX_SIZE

#define __MAX__                 BUFFER_SIZE + 4

; pre-DEFd
#define PLAYER_BASE_BLOCK       __MAX__ + 0x18 ; {r3-r7,lr} take up the other 24 bytes

    push    {r3-r7, lr}
    sub     sp, #__MAX__
    str     r0, [sp, #TRAINER_ID]
    str     r1, [sp, #S_PartyPtr]
    str     r2, [sp, #S_HeapId]
    
    mov     r0, r1 ; S_PartyPtr
    mov     r1, #6 ; count
    bl      ARM9::PrepareFullPartyBlock
    
    ldr     r2, [sp, #S_HeapId]
    ldr     r1, =0x7FFF ; Mask: 01111111_11111111
    and     r2, r1
    
    add     r0, r1, #1 ; 10000000_00000000
    mov     r4, r2
    orr     r4, r0 ; r4 := HeapId | 0x8000
    
    ; Allocate trdata file
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNumber]
    mov     r0, r4
    mov     r1, #Trainer.SIZE
    mov     r2, #0 ; clear block = false
    ldr     r3, =ARM9::Data_tr_tool
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #S_TrainerData]
    
    ldrh    r1, [r0, #TrainerData.pokeDataSize]
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNumber]
    mov     r0, r4
    mov     r2, #0 ; clear block = false
    ldr     r3, =ARM9::Data_tr_tool
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #S_TrainerPokeData]
    
    bl      ARM9::GetPartyBlockSize ; always returns 220 (36 * 6)
    mov     r1, r0 ; block size
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNumber]
    
    mov     r0, r4
    mov     r2, #0 ; clear block = false
    ldr     r3, =ARM9::Data_tr_tool
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #PARTY_POKES]
    
    lsl     r0, r4, #16
    lsr     r0, #16
    mov     r1, #32
    mov     r2, #0 ; clear block = false
    mov     r3, r6 ; tr_tool.c
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #SELECTABLE_POKES]
    
    ldr     r0, [sp, #TRAINER_ID]
    ldr     r1, [sp, #S_TrainerData]
    bl      ARM9::LoadTrainerFile
    
    ldr     r0, [sp, #TRAINER_ID]
    ldr     r1, [sp, #S_TrainerPokeData]
    bl      ARM9::LoadTrainerPokeFile
    
    ldr     r0, [sp, #S_TrainerData]
    ldrb    r0, [r0, #1] ; trainer class
    bl      ARM9::GetTrainerClassGender ; gender is determined by trainer class; 0=male, 1=female
    mov     r1, #120
    cmp     r0, #0
    bne     StorePokeFlagOffset
    mov     r1, #136    
StorePokeFlagOffset:
    str     r1, [sp, #S_TrainerFlagsOffset]
    

    ; Normally, Trainer Flags are 000000IM where I = custom items and M = custom moves
    ; our updated version is 0RREIIMM
    ;   M: move mode    0=Level, 1=Custom, 2=Random, 3=RandomTMs
    ;   I: item mode    0=NoItems, 1=Custom, 2=RandomEasy, 3=RandomHard
    ;   E: custom evs
    ;   R: random mode  0=Off, 2=Locked (based solely on TID), 3=Always (random every time)
    #define MOVE_MODE_LEVEL 0
    #define MOVE_MODE_CUSTOM 1
    #define MOVE_MODE_RANDOM 2
    #define MOVE_MODE_RANDOM_TMS 3
    
    #define ITEM_MODE_NONE 0
    #define ITEM_MODE_CUSTOM 1
    #define ITEM_MODE_RANDOM_EASY 2
    #define ITEM_MODE_RANDOM_HARD 3
    
    #define RANDOM_MODE_OFF 0
    #define RANDOM_MODE_LOCKED 2
    #define RANDOM_MODE_ALWAYS 3

    ldr     r0, [sp, #S_TrainerData]
    ldrb    r5, [r0] ; trainer flags
    mov     r1, #0x08 ; POKE_SIZE
    
ItemMode:
    mov     r0, #-1
    str     r0, [sp, #POKE_ITEM_OFFSET]
    lsl     r0, r5, #28
    lsr     r0, #30
    cmp     r0, #ITEM_MODE_CUSTOM
    bne     MoveMode
    str     r1, [sp, #POKE_ITEM_OFFSET]
    add     r1, #2

MoveMode:
    mov     r0, #-1
    str     r0, [sp, #POKE_MOVES_OFFSET]
    lsl     r0, r5, #30
    lsr     r0, #30
    cmp     r0, #MOVE_MODE_CUSTOM
    bne     EVs
    str     r1, [sp, #POKE_MOVES_OFFSET]
    add     r1, #8
    
EVs:
    mov     r0, #-1
    str     r0, [sp, #POKE_EVS_OFFSET]
    lsl     r0, r5, #27
    lsr     r0, #31
    cmp     r0, #FALSE
    beq     RandomMode
    str     r1, [sp, #POKE_EVS_OFFSET]
    add     r1, #2

RandomMode:
    lsl     r0, r5, #25
    lsr     r0, #30
    str     r0, [sp, #POKE_RANDOM_MODE]
    
StorePokeSize:
    str     r1, [sp, #POKE_SIZE]
    

ClearSelectedPokes:
    mov     r0, #0
    str     r0, [sp, #SELECTED_COUNT]


SetUpLCG:
    mov     r0, [sp, #PLAYER_BASE_BLOCK]
    bl      ARM9::JumpToPlayerBaseBlockInfo
    bl      ARM9::GetIDAsUInt
    ldr     r1, [sp, #S_TrainerNum]
    add     r0, r1
    str     r0, [sp, #S_LcgState_Low]
    mov     r1, #0
    str     r1, [sp, #S_LcgState_High]
    bl      ARM9::SeedLCG
    str     r0, [sp, #S_LcgState_Low]
    str     r1, [sp, #S_LcgState_High]


SetUpChannelLoop:
    ldr     r0, [sp, #S_TrainerData]
    ldrb    r1, [r0, #3]
    str     r1, [sp, #POOL_SIZE]
    mov     r0, #0 ; index
    str     r0, [sp, #CHANNEL]
    
ChannelLoopStart:
    mov     r0, #0
    str     r0, [sp, #BUFFER_SIZE]
    str     r0, [sp, #POKE_INDEX]
    
    ; TODO: Make it so channels select the desired number of Pokémon from the pool
    
ChannelLoop_PokeLoopStart:
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
    mul     r1, r0
    ldr     r0, [sp, #S_TrainerPokeData]
    add     r1, r0
    ldrb    r0, [r1, #3] ; channel
    ldr     r1, [sp, #CHANNEL]
    cmp     r0, r1
    bne     ChannelLoop_PokeLoopEnd ; this poke doesn't use this channel
    
; add poke to buffer
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #BUFFER]
    ldr     r2, [sp, #BUFFER_SIZE]
    str     r0, [r1, r2]
    add     r2, #1
    str     r2, [sp, #BUFFER_SIZE]    

ChannelLoop_PokeLoopEnd:
    ldr     r0, [sp, #POKE_INDEX]
    add     r0, #1
    str     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POOL_SIZE]
    cmp     r0, r1
    bcc     ChannelLoop_PokeLoopStart
    
; Select a Pokémon from the buffer...
; First, choose a random algo
    ldr     r0, [sp, #POKE_RANDOM_MODE]
    cmp     r0, #RANDOM_MODE_LOCKED
    bne     ChannelLoop_Mersenne

ChannelLoop_LCG:
    ldr     r0, [sp, #S_LcgState_Low]
    ldr     r1, [sp, #S_LcgState_High]
    bl      ARM9::LCG
    mov     r0, r1
    b       ChannelLoop_SelectPoke
    
ChannelLoop_Mersenne:
    bl      ARM9::MersenneTwister
    
ChannelLoop_SelectPoke:
    ldr     r0, [sp, #S_TrainerData]
    ldrb    r1, [r0, #3]

ChannelLoopEnd:
    ldr     r0, [sp, #CHANNEL]
    add     r0, #1
    str     r0, [sp, #CHANNEL]
    cmp     r0, #6
    bcc     ChannelLoopStart
    
    





    
    
    
    
    
    
    
    
    
    
    
    
    
; ACE
FindAces:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX]
    mov     r3, #0 ; write_offset
    
FindAcesLoopStart:
    ldr     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
FindAcesLoopEnd:
    add     r2, #1
    str     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_COUNT]
    cmp     r0, r1
    blt     FindAcesLoopStart
    
    
; LEADS
FindLeads:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX]
    mov     r3, #0 ; write_offset
    
FindLeadsLoopStart:
    ldr     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
    mul     r1, r2
    ldr     r0, [sp, #S_TrainerPokeData]
    add     r1, r0
    ldrb    r0, [r1, #1] ; flags
    lsl     r0, #28
    lsr     r0, #30 ; Mask: 00001100
    cmp     r0, #1
    bne     FindLeadsLoopEnd ; continue
    add     r0, sp, #SELECTABLE_POKES
    str     r2, [r0, r3]
    add     r3, #4 ; write_offset += 4
FindLeadsLoopEnd:
    add     r2, #1
    str     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_COUNT]
    cmp     r0, r1
    blt     FindLeadsLoopStart

SelectLeads:
    mov     r3, #0 ; index
    
    ldr     r0, [sp, #S_TrainerData]
    ldrb    r0, [r0, #0]
    lsl     r0, #25
    lsr     r0, #30 ; Mask: 01100000
    
    
    
SetupLoop:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX] ; i := 0
    
LoopStart:
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r5, [sp, #POKE_SIZE]
    mul     r5, r0
    ldr     r0, [sp, #S_TrainerPokeData]
    add     r6, r0, r5
    
    ldr     r0, [sp, #BLOCK_GROUP_ID]
    str     r0, [sp, #S_ArgHeapId]
    ldrh    r0, [r6, #4] ; species
    ldrh    r1, [r6, #6] ; form
    ldrb    r2, [r6, #1] ; poke flags
    add     r3, sp, #S_TrainerFlagsOffset ; tr_flags*
    bl      ARM9::HandlerTrainerPokeFlags
    
    ldrh    r0, [r6, #4] ; species
    str     r0, [sp, #POKE_SPECIES]
    
    ldrb    r0, [r6, #2] ; level
    str     r0, [sp, #POKE_LEVEL]
    
    ldrb    r0, [r6, #3] ; random id
    str     r0, [sp, #POKE_RANDOM_ID]
    
    ldrb    r0, [r6, #0] ; difficulty
    str     r0, [sp, #POKE_DIFFICULTY]
    
    ldrb    r0, [r6, #1] ; flags
    str     r0, [sp, #POKE_FLAGS]
    
    ; Randomness is sources from the player's Trainer ID. This allows for many random factors to remain consistent
    ; within one playthrough (an NPC having the same Pokémon) but different for a other save files.
    mov     r0, [sp, #PLAYER_BASE_BLOCK]
    bl      ARM9::JumpToPlayerBaseBlockInfo
    bl      ARM9::GetIDAsUInt
    ldr     r1, [sp, #S_TrainerNum]
    add     r0, r1
    ldr     r1, [r6, #4] ; species
    add     r0, r1
    ldrb    r1, [r6, #3] ; random_id
    add     r3, r0, r1 ; r3 := player_tid + trainer_num + poke_species + poke_random_id
    
    mov     r5, #0 ; lcg iteration
    
    ldr     r0, [sp, #S_TrainerData]
    ldrb    r7, [r0, #1] ; trainer class
    
    cmp     r7, #0
    ble     LinearCongruentialEnd ; trainer_class <= 0
    
SeedLinearCongruential:
    mov     r0, r3 ; seed (low)
    mov     r1, #0 ; seed (high)
    bl      ARM9::SeedLCG
    str     r0, [sp, #S_LcgState_Low]
    str     r1, [sp, #S_LcgState_High]
    
LinearCongruentialEnd:
    ldr     r0, [sp, #S_TrainerFlagsOffset]
    lsl     r1, r2, #8
    add     r0, r1 ; 0x00RRRRFF, R=rand, F=flags
    str     r0, [sp, #POKE_ID]
    
IVs:
    
    mov     r5, #31
    
    
End:
    ldr     r0, [sp, #S_TrainerData]
    bl      ARM9::FreeBlock
    
    ldr     r0, [sp, #S_TrainerPokeData]
    bl      ARM9::FreeBlock
    
    ldr     r0, [sp, #SELECTABLE_POKES]
    bl      ARM9::FreeBlock
    
    mov     r0, r4
    bl      ARM9::FreeBlock
    
    add     sp, #__MAX__
    pop     {r3-r7, pc}