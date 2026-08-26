package pk.lgsarcmun.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) cameraPermission.launch(Manifest.permission.CAMERA); setContent { HubApp() } }
}

@Composable fun HubApp() {
    var tab by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF4DAEFF), background = Color(0xFF050A12), surface = Color(0xFF0C1624))) {
        Scaffold(containerColor = Color(0xFF050A12), bottomBar = { NavigationBar(containerColor = Color(0xFF08111D)) { listOf("Home","Scan","Membership","More").forEachIndexed { i, label -> NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Text(listOf("⌂","⌁","▣","•••")[i],fontSize=20.sp)},label={Text(label)}) } } }) { p -> Box(Modifier.padding(p).fillMaxSize()) { when(tab){0->HomeScreen{tab=1};1->ScanScreen();2->MembershipScreen();else->MoreScreen()} } }
    }
}
@Composable fun Header(){Column(Modifier.padding(horizontal=22.dp,vertical=24.dp)){Text("LGSARCMUN",color=Color(0xFF66B9FF),fontSize=13.sp,fontWeight=FontWeight.Bold,letterSpacing=3.sp);Text("Hub",color=Color.White,fontSize=38.sp,fontWeight=FontWeight.Bold);Text("Your official society companion",color=Color(0xFF92A5B8),fontSize=14.sp)}}
@Composable fun HomeScreen(onScan:()->Unit){Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A1B30),Color(0xFF050A12))))){Header();Card(Modifier.padding(20.dp).fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Color(0xFF10253B)),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(24.dp)){Text("Welcome to LGSARCMUN Hub",color=Color.White,fontSize=22.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Text("Membership, identity and society tools — all in one place.",color=Color(0xFFAFC0D1));Spacer(Modifier.height(20.dp));Button(onClick=onScan,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp)){Text("SCAN MEMBERSHIP",fontWeight=FontWeight.Bold)}}};Text("Quick access",color=Color.White,fontWeight=FontWeight.Bold,fontSize=18.sp,modifier=Modifier.padding(22.dp,20.dp,22.dp,12.dp));Row(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){QuickCard("My Membership","View card");QuickCard("Activity","Recent scans")}}}
@Composable fun QuickCard(a:String,b:String){Card(Modifier.weight(1f),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF0D1724))){Column(Modifier.padding(16.dp)){Text(a,color=Color.White,fontWeight=FontWeight.Bold);Text(b,color=Color(0xFF7F93A8),fontSize=12.sp,modifier=Modifier.padding(top=6.dp))}}}
@Composable fun ScanScreen(){Column(Modifier.fillMaxSize().background(Color(0xFF050A12)),horizontalAlignment=Alignment.CenterHorizontally){Text("Scan Membership",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=30.dp));Text("Point the camera at an LGSARCMUN QR code",color=Color(0xFF91A4B8),modifier=Modifier.padding(8.dp));Box(Modifier.padding(30.dp).fillMaxWidth().aspectRatio(1f).background(Color(0xFF0D1724),RoundedCornerShape(28.dp)),contentAlignment=Alignment.Center){Text("QR SCAN AREA",color=Color(0xFF66B9FF),fontWeight=FontWeight.Bold,fontSize=18.sp)};Button(onClick={},shape=RoundedCornerShape(14.dp)){Text("ENABLE CAMERA")}}}
@Composable fun MembershipScreen(){Column(Modifier.fillMaxSize().background(Color(0xFF050A12))){Text("My Membership",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(24.dp));Card(Modifier.padding(20.dp).fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Color(0xFF10253B)),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(24.dp)){Text("MEMBERSHIP STATUS",color=Color(0xFF71C998),fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.5.sp);Text("Sign in to view your digital membership card.",color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=12.dp));Button(onClick={},modifier=Modifier.padding(top=20.dp).fillMaxWidth()){Text("SIGN IN / SIGN UP")}}}} 
@Composable fun MoreScreen(){Column(Modifier.fillMaxSize().background(Color(0xFF050A12))){Text("More",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(24.dp));listOf("Announcements","Settings","Help & Support","About LGSARCMUN Hub").forEach{Text(it,color=Color.White,fontSize=17.sp,modifier=Modifier.fillMaxWidth().padding(20.dp))}}}
