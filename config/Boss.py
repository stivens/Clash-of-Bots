import random
import sys

while True:
    num_robots = int(input())
    for _ in range(num_robots):
        for y in range(3):
            row = input()
        print(f"{random.choice(['GUARD', 'SELFDESTRUCTION', 'ATTACK UP', 'ATTACK DOWN', 'ATTACK LEFT', 'ATTACK RIGHT', 'MOVE UP', 'MOVE DOWN', 'MOVE LEFT', 'MOVE RIGHT'])}")
        #print(f"{random.choice(['ATTACK', 'MOVE'])} {random.choice(['UP', 'DOWN', 'LEFT', 'RIGHT'])}")
    sys.stdout.flush()
