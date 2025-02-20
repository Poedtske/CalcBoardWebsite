import asyncio
import datetime
import json
import base64
import os
from dotenv import load_dotenv
from spond import spond

load_dotenv()

# Fetch sensitive data from .env file
username = os.getenv('SPOND_USERNAME')
password = os.getenv('SPOND_PASSWORD')
group_id = os.getenv('SPOND_GROUP_ID')
api_base_url = os.getenv('URL')  # Fetch the base URL from the environment file

# Spring user credentials
usernameSpring = "John.Doe@email.com"
passwordSpring = "2424"
cred = f"{usernameSpring}:{passwordSpring}"
encoded_credentials = base64.b64encode(cred.encode("utf-8")).decode("utf-8")

# Define the headers that will be used in every API call
headers = {
    "Content-Type": "application/json",  # Replace with your actual API key
    "Authorization": f"Basic {encoded_credentials}",
}

def add_two_hours_to_timestamp(timestamp):
    # Convert timestamp to datetime object, add 2 hours, and return formatted string
    dt = datetime.datetime.fromisoformat(timestamp.replace('Z', '+00:00'))
    dt += datetime.timedelta(hours=2)  # Adjust to 2 hours
    return dt.strftime('%Y-%m-%d %H:%M:%S.0')

async def get_events_spond():
    s = spond.Spond(username=username, password=password)
    group = await s.get_group(group_id)
    events = await s.get_events(group_id=group_id, include_scheduled=True, min_start=datetime.datetime.now())
    await s.clientsession.close()
    eventlist = []

    for event in events:
        event_details = {
            "spondId": event['id'],
            "title": event['heading'],
            "startTime": add_two_hours_to_timestamp(event['startTimestamp']),
            "endTime": add_two_hours_to_timestamp(event['endTimestamp']),
            "location": event['location']['feature']
        }

        # Add description if it exists
        if 'description' in event:
            event_details['description'] = event['description']

        # Access subgroups from recipients -> group -> subGroups
        if 'recipients' in event and 'group' in event['recipients'] and 'subGroups' in event['recipients']['group']:
            subgroups = event['recipients']['group']['subGroups']
            if subgroups:  # Check if there are subgroups
                event_details['subGroups'] = [
                    {
                        "subgroupId": subgroup['id'],
                        "subgroupName": subgroup['name']
                    }
                    for subgroup in subgroups
                ]
            else:
                event_details['subGroups'] = []  # If no subgroups exist

        eventlist.append(event_details)

    return eventlist

async def main():
    spond_events = await get_events_spond()
    
    if spond_events:
        # Print the first event with its subgroups if any
        print(json.dumps(spond_events, indent=4))  # Pretty print the first event
    else:
        print("No events found or there was an error.")

if __name__ == "__main__":
    asyncio.run(main())
