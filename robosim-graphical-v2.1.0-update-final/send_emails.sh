SERVER=us16
DATE=$(date +"%d%h%Y-%H:%M:%S")
# robochart-update

## CHANGE THIS
TID=10026402 # template

## CHANGE THIS
# LID=234a3b02e7 # test list
LID=a67ed5ba2c # real RoboTool list

## CHANGE THIS
# SID=2215126 # test segment
SID=2215106 # real RoboChart Core segment

## CHANGE THIS
SUBJECT="RoboSim Core Plugins Update"

## CHANGE THIS
PREVIEW="The RoboSim Core Plugins have been updated."

## CHANGE THIS
TITLE="robosim-core-plugins-'${DATE}'"

CREATION_DATA='{"type":"regular","recipients":{"segment_opts":{"saved_segment_id":'$SID'},"list_id":"'$LID'"},"settings":{"subject_line":"'$SUBJECT'","title":"'${TITLE}'","from_name":"RoboTool","reply_to":"robocharttool@gmail.com","auto_tweet":false,"auto_fb_post":[],"fb_comments":false,"template_id":'${TID}'},"content_type":"template"}'

echo $CREATION_DATA | jq -r .

RESULT=$(curl -Ss -X POST \
  https://${SERVER}.api.mailchimp.com/3.0/campaigns \
  --user "${MAILCHIMPUSR}:${MAILCHIMPKEY}" \
  -d "${CREATION_DATA}")
  
echo "Result of campaign creation is"
echo $RESULT | jq -r .
  
CID=$(echo $RESULT | jq -r '.id')

if [ ! -n "$CID" ] || [ "$CID" == "null" ]
then
	echo "Failed to create new campaign"
else
echo "Sending campaign with id "$CID
SEND_RESULT=$(curl -Ss -X POST \
  https://${SERVER}.api.mailchimp.com/3.0/campaigns/${CID}/actions/send \
  --user "$MAILCHIMPUSR:$MAILCHIMPKEY")
echo "Result of sending the campaign is"
echo $SEND_RESULT | jq -r .
fi
